package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class ConcurrentExecution extends HeterogeneousTasksExecution {

    public static Behavior<TaskCommand> create(Task[] tasks) {
        return Behaviors.setup(ctx -> new ConcurrentExecution(ctx, tasks));
    }

    private final Task[] tasks;
    private final ConcurrentExecutionState state;

    private ActorRef<ProcessorCommand> responseAdapter;

    private ConcurrentExecution(ActorContext<TaskCommand> context, Task[] tasks) {
        super(context);
        this.tasks = tasks;
        this.state = new ConcurrentExecutionState();

        this.responseAdapter = context.messageAdapter(ProcessorCommand.class, InternalAdaptedProcessorCommand::new);
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(InternalAdaptedProcessorCommand.class, this::onInternalAdaptedProcessorCommand);
        builder.onMessage(Flush.class, this::onFlush);
        builder.onMessage(TaskTimeout.class, this::onTaskTimeout);
        return builder.build();
    }

    @Override
    protected TasksExecutionState getState() {
        return state;
    }

    public Behavior<TaskCommand> onFlush(Flush msg) {
        for (Task task : tasks) {
            if (task.isFlushable() && isTaskActive(task)) {
                ActorRef<TaskCommand> taskRef = getTaskRef(task);
                taskRef.tell(msg);
            }
        }
        return Behaviors.same();
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
        int tasksAccepted = 0;
        JobTaskToProcess adaptedMsg = new JobTaskToProcess(msg, responseAdapter);
        for (Task task : this.tasks) {
            if (task.accept(msg)) {
                tasksAccepted++;
                sendToTask(task, adaptedMsg);
            }
        }
        if (tasksAccepted > 0) {
            state.taskStarted(msg.itemURI, msg.replyTo, tasksAccepted);
        } else {
            msg.replyTo.tell(new JobTaskFinished(msg.uri, msg.itemURI, ProcessingContext.EMPTY));
        }
        return Behaviors.same();
    }

    private Behavior<TaskCommand> onInternalAdaptedProcessorCommand(InternalAdaptedProcessorCommand msg) {
        if (msg.processorCommand instanceof JobTaskFinished) {
            onJobTaskFinished((JobTaskFinished) msg.processorCommand);
        } else if (msg.processorCommand instanceof JobTaskFailed) {
            onJobTaskFailed((JobTaskFailed) msg.processorCommand);
        } else {
            throw new IllegalArgumentException("Impossible to handle " + msg.processorCommand.getClass().getName());
        }
        return Behaviors.same();
    }

    private void onJobTaskFinished(JobTaskFinished msg) {
        state.mergeContext(msg.itemURI, msg.ctx);
        checkTaskFinished(msg.uri, msg.itemURI);
    }

    private void onJobTaskFailed(JobTaskFailed msg) {
        state.mergeContext(msg.itemURI, msg.ctx);
        checkTaskFinished(msg.uri, msg.itemURI);
    }

    public Behavior<TaskCommand> onTaskTimeout(TaskTimeout msg) {
        state.mergeContext(msg.itemURI, msg.ctx);
        checkTaskFinished(msg.uri, msg.itemURI);
        return Behaviors.same();
    }

    protected void checkTaskFinished(URI uri, URI itemURI) {
        state.taskFinished(itemURI);
        if (state.isTaskFinished(itemURI)) {
            ActorRef<ProcessorCommand> replyTo = state.getReplyTo(itemURI);
            ProcessingContext ctx = state.getProcessingContext(itemURI);
            replyTo.tell(new JobTaskFinished(uri, itemURI, ctx));
            state.finish(itemURI);
        }
    }

    private static class InternalAdaptedProcessorCommand implements TaskCommand {

        public final ProcessorCommand processorCommand;

        public InternalAdaptedProcessorCommand(ProcessorCommand msg) {
            super();
            this.processorCommand = msg;
        }

    }

}
