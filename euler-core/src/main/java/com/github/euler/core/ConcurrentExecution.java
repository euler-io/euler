package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class ConcurrentExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Task[] tasks) {
        return Behaviors.setup(ctx -> new ConcurrentExecution(ctx, tasks));
    }

    private final Task[] tasks;
    private final ConcurrentExecutionState state;

    private Map<String, ActorRef<TaskCommand>> mapping;
    private ActorRef<ProcessorCommand> responseAdapter;

    private ConcurrentExecution(ActorContext<TaskCommand> context, Task[] tasks) {
        super(context);
        this.tasks = tasks;
        this.mapping = new HashMap<>();
        this.state = new ConcurrentExecutionState();

        this.responseAdapter = context.messageAdapter(ProcessorCommand.class, InternalAdaptedProcessorCommand::new);
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(InternalAdaptedProcessorCommand.class, this::onInternalAdaptedProcessorCommand);
        builder.onMessage(InternalJobTaskFailed.class, this::onInternalJobTaskFailed);
        return builder.build();
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
        for (Task task : this.tasks) {
            JobTaskToProcess adaptedMsg = new JobTaskToProcess(msg, responseAdapter);
            if (task.accept(msg)) {
                ActorRef<TaskCommand> ref = getTaskRef(task, msg);
                ref.tell(adaptedMsg);
                state.taskStarted(msg.itemURI, msg.replyTo);
            }
        }
        return this;
    }

    private ActorRef<TaskCommand> getTaskRef(Task task, JobTaskToProcess msg) {
        ActorRef<TaskCommand> ref = mapping.computeIfAbsent(task.name(), (k) -> getContext().spawn(superviseTaskBehavior(task), k));
        getContext().watchWith(ref, new InternalJobTaskFailed(msg, task.name()));
        return ref;
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    private Behavior<TaskCommand> onInternalAdaptedProcessorCommand(InternalAdaptedProcessorCommand msg) {
        if (msg.processorCommand instanceof JobTaskFinished) {
            onJobTaskFinished((JobTaskFinished) msg.processorCommand);
        } else {
            throw new IllegalArgumentException("Impossible to handle " + msg.processorCommand.getClass().getName());
        }

        return this;
    }

    private void onJobTaskFinished(JobTaskFinished msg) {
        checkTaskFinished(msg.uri, msg.itemURI);
    }

    private Behavior<TaskCommand> onInternalJobTaskFailed(InternalJobTaskFailed msg) {
        mapping.remove(msg.taskName);
        // msg.replyTo.tell(new JobTaskFailed(msg.uri, msg.itemURI));
        checkTaskFinished(msg.uri, msg.itemURI);
        return Behaviors.same();
    }

    protected void checkTaskFinished(URI uri, URI itemURI) {
        state.taskFinished(itemURI);
        if (state.isTaskFinished(itemURI)) {
            ActorRef<ProcessorCommand> replyTo = state.getReplyTo(itemURI);
            replyTo.tell(new JobTaskFinished(uri, itemURI, ProcessingContext.EMPTY));
        }
    }

    private static class InternalAdaptedProcessorCommand implements TaskCommand {

        public final ProcessorCommand processorCommand;

        public InternalAdaptedProcessorCommand(ProcessorCommand msg) {
            super();
            this.processorCommand = msg;
        }

    }

    private static class InternalJobTaskFailed implements TaskCommand {

        public final URI uri;
        public final URI itemURI;
        public final String taskName;
        // public final ActorRef<ProcessorCommand> replyTo;

        public InternalJobTaskFailed(JobTaskToProcess msg, String taskName) {
            this.uri = msg.uri;
            this.itemURI = msg.itemURI;
            // this.replyTo = msg.replyTo;
            this.taskName = taskName;
        }

    }

}
