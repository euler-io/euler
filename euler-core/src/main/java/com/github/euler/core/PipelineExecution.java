package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class PipelineExecution extends TasksExecution {

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
        // builder.onMessage(InternalJobTaskFailed.class,
        // this::onInternalJobTaskFailed);
        builder.onMessage(Flush.class, this::onFlush);
        builder.onMessage(TaskTimedout.class, this::onTaskTimedout);
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

    public Behavior<TaskCommand> onTaskTimedout(TaskTimedout msg) {
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
        } else {
            throw new IllegalArgumentException("Impossible to handle " + msg.processorCommand.getClass().getName());
        }

        return this;
    }

    private void onJobTaskFailed(JobTaskFailed msg) {
        onFail(msg.uri, msg.itemURI, msg.ctx);
    }

    private void onFail(URI uri, URI itemURI, ProcessingContext ctx) {
        ActorRef<ProcessorCommand> replyTo = state.getReplyTo(itemURI);
        ctx = state.mergeContext(itemURI, ctx);
        replyTo.tell(new JobTaskFinished(uri, itemURI, ctx));
        state.finish(itemURI);
    }

    private void onJobTaskFinished(JobTaskFinished msg) {
        ProcessingContext ctx = state.mergeContext(msg.itemURI, msg.ctx);
        sendToNextOrFinish(new JobTaskToProcess(msg.uri, msg.itemURI, ctx, responseAdapter));
    }

    // private Behavior<TaskCommand>
    // onInternalJobTaskFailed(InternalJobTaskFailed msg) {
    // mapping.remove(msg.taskName);
    // ActorRef<ProcessorCommand> replyTo = state.getReplyTo(msg.itemURI);
    // ProcessingContext ctx = state.getProcessingContext(msg.itemURI);
    // replyTo.tell(new JobTaskFinished(msg.uri, msg.itemURI, ctx));
    // state.finish(msg.itemURI);
    // return Behaviors.same();
    // }

    private void sendToNextOrFinish(JobTaskToProcess msg) {
        Task task = getNextTask(msg);
        if (task != null) {
            // ActorRef<TaskCommand> taskRef = getOrSpawnTaskRef(task);
            JobTaskToProcess adaptedMsg = new JobTaskToProcess(msg.uri, msg.itemURI, msg.ctx, responseAdapter);
            // taskRef.tell(adaptedMsg);
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

    // private ActorRef<TaskCommand> getTaskRef(Task task) {
    // return mapping.get(task.name());
    // }

    // private ActorRef<TaskCommand> getOrSpawnTaskRef(Task task,
    // JobTaskToProcess msg) {
    // ActorRef<TaskCommand> ref = mapping.computeIfAbsent(task.name(), (k) ->
    // getContext().spawn(superviseTaskBehavior(task), k));
    // TODO supervision must be reimplemented.
    // getContext().watchWith(ref, new InternalJobTaskFailed(msg,
    // task.name()));
    // return ref;
    // }

    // private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
    // Behavior<TaskCommand> behavior =
    // Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.restart());
    // return behavior;
    // }

    private static class InternalAdaptedProcessorCommand implements TaskCommand {

        public final ProcessorCommand processorCommand;

        public InternalAdaptedProcessorCommand(ProcessorCommand msg) {
            super();
            this.processorCommand = msg;
        }

    }

}
