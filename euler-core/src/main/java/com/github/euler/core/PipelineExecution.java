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

public class PipelineExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Task[] tasks) {
        return Behaviors.setup((context) -> new PipelineExecution(context, tasks));
    }

    private final Task[] tasks;
    private PipelineExecutionState state;

    private Map<String, ActorRef<TaskCommand>> mapping;
    private ActorRef<ProcessorCommand> responseAdapter;

    public PipelineExecution(ActorContext<TaskCommand> context, Task[] tasks) {
        super(context);
        this.tasks = tasks;
        this.state = new PipelineExecutionState();
        this.mapping = new HashMap<>();

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
        state.onMessage(msg);
        sendToNextOrFinish(msg);
        return this;
    }

    private Behavior<TaskCommand> onInternalAdaptedProcessorCommand(InternalAdaptedProcessorCommand msg) {
        if (msg.processorCommand instanceof JobTaskFinished) {
            onJobTaskFinished((JobTaskFinished) msg.processorCommand);
        } else if (msg.processorCommand instanceof JobTaskFailed) {
            onJobTaskFailed((JobTaskFailed) msg.processorCommand);
        } else {
            throw new IllegalArgumentException("Impossible to handle " + msg.processorCommand.getClass().getName());
        }

        return this;
    }

    private void onJobTaskFailed(JobTaskFailed msg) {
        ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.itemURI);
        ProcessingContext ctx = state.mergeContext(msg.itemURI, msg.ctx);
        replyTo.tell(new JobTaskFinished(msg.uri, msg.itemURI, ctx));
    }

    private Behavior<TaskCommand> onInternalJobTaskFailed(InternalJobTaskFailed msg) {
        mapping.remove(msg.taskName);
        ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.itemURI);
        ProcessingContext ctx = state.getProcessingContext(msg.itemURI);
        replyTo.tell(new JobTaskFinished(msg.uri, msg.itemURI, ctx));
        return Behaviors.same();
    }

    private void onJobTaskFinished(JobTaskFinished msg) {
        ProcessingContext ctx = state.mergeContext(msg.itemURI, msg.ctx);
        sendToNextOrFinish(new JobTaskToProcess(msg.uri, msg.itemURI, ctx, responseAdapter));
    }

    private void sendToNextOrFinish(JobTaskToProcess msg) {
        Task task = getNextTask(msg);
        if (task != null) {
            ActorRef<TaskCommand> taskRef = getTaskRef(task, msg);
            JobTaskToProcess adaptedMsg = new JobTaskToProcess(msg.uri, msg.itemURI, msg.ctx, responseAdapter);
            taskRef.tell(adaptedMsg);
        } else {
            ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.itemURI);
            replyTo.tell(new JobTaskFinished(msg, msg.ctx));
        }
    }

    private Task getNextTask(JobTaskToProcess msg) {
        Task task = null;
        int position = state.getPosition(msg.itemURI);
        while (task == null && position < tasks.length) {
            if (tasks[position].accept(msg)) {
                task = tasks[position];
            }
            position++;
            state.setPosition(msg.itemURI, position);
        }
        return task;
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

        public InternalJobTaskFailed(JobTaskToProcess msg, String taskName) {
            this.uri = msg.uri;
            this.itemURI = msg.itemURI;
            this.taskName = taskName;
        }

    }

}
