package com.github.euler.core;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class PipelineExecution extends HeterogeneousTasksExecution {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static Behavior<TaskCommand> create(Task[] tasks) {
        return Behaviors.setup((context) -> new PipelineExecution(context, tasks));
    }

    private final Task[] tasks;
    private PipelineExecutionState state;

    private ActorRef<ProcessorCommand> responseAdapter;

    public PipelineExecution(ActorContext<TaskCommand> context, Task[] tasks) {
        super(context);
        this.tasks = tasks;
        this.state = new PipelineExecutionState();

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

    public Behavior<TaskCommand> onTaskTimeout(TaskTimeout msg) {
        onFail(msg.uri, msg.itemURI, msg.ctx);
        return Behaviors.same();
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
        } else if (msg.processorCommand instanceof EmbeddedItemFound) {
            onEmbeddedItemFound((EmbeddedItemFound) msg.processorCommand);
        } else {
            throw new IllegalArgumentException("Impossible to handle " + msg.processorCommand.getClass().getName());
        }

        return this;
    }

    private void onEmbeddedItemFound(EmbeddedItemFound msg) {
        ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.parentURI);
        replyTo.tell(msg);
    }

    private void onJobTaskFailed(JobTaskFailed msg) {
        onFail(msg.uri, msg.itemURI, msg.ctx);
    }

    private void onFail(URI uri, URI itemURI, ProcessingContext ctx) {
        LOGGER.warn("{} failed,", itemURI);
        ActorRef<ProcessorCommand> replyTo = state.getReplyTo(itemURI);
        ctx = state.mergeContext(itemURI, ctx);
        replyTo.tell(new JobTaskFinished(uri, itemURI, ctx));
        state.finish(itemURI);
    }

    private void onJobTaskFinished(JobTaskFinished msg) {
        ProcessingContext ctx = state.mergeContext(msg.itemURI, msg.ctx);
        sendToNextOrFinish(new JobTaskToProcess(msg.uri, msg.itemURI, ctx, responseAdapter));
    }

    private void sendToNextOrFinish(JobTaskToProcess msg) {
        Task task = getNextTask(msg);
        if (task != null) {
            JobTaskToProcess adaptedMsg = new JobTaskToProcess(msg.uri, msg.itemURI, msg.ctx, responseAdapter);
            sendToTask(task, adaptedMsg);
        } else {
            ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.itemURI);
            replyTo.tell(new JobTaskFinished(msg, msg.ctx));
            state.finish(msg.itemURI);
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

    private static class InternalAdaptedProcessorCommand implements TaskCommand {

        public final ProcessorCommand processorCommand;

        public InternalAdaptedProcessorCommand(ProcessorCommand msg) {
            super();
            this.processorCommand = msg;
        }

    }

}
