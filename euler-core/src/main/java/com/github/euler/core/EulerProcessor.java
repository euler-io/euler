package com.github.euler.core;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerProcessor extends AbstractBehavior<ProcessorCommand> {

    public static Behavior<ProcessorCommand> create(Task... tasks) {
        return Behaviors.setup(ctx -> new EulerProcessor(ctx, tasks));
    }

    private Task[] tasks;
    private Map<String, ActorRef<TaskCommand>> mapping;

    private EulerProcessorState state;

    private EulerProcessor(ActorContext<ProcessorCommand> ctx, Task... tasks) {
        super(ctx);
        this.tasks = tasks;
        this.mapping = new HashMap<>();
        this.state = new EulerProcessorState();
    }

    @Override
    public Receive<ProcessorCommand> createReceive() {
        ReceiveBuilder<ProcessorCommand> builder = newReceiveBuilder();
        builder.onMessage(JobItemToProcess.class, this::onJobItemToProcess);
        builder.onMessage(JobTaskFinished.class, this::onJobTaskFinished);
        builder.onMessage(JobTaskFailed.class, this::onJobTaskFailed);
        builder.onMessage(InternalJobTaskFailed.class, this::onInternalJobTaskFailed);
        builder.onMessage(Flush.class, this::onFlush);
        return builder.build();
    }

    public Behavior<ProcessorCommand> onFlush(Flush msg) {
        for (Task task : tasks) {
            if (task.isFlushable() && mapping.containsKey(task.name())) {
                ActorRef<TaskCommand> taskRef = getTaskRef(task);
                taskRef.tell(msg);
            }
        }
        return Behaviors.same();
    }

    public Behavior<ProcessorCommand> onJobItemToProcess(JobItemToProcess msg) {
        state.onJobItemToProcess(msg);
        distributeToTasks(msg);
        return Behaviors.same();
    }

    public Behavior<ProcessorCommand> onJobTaskFinished(JobTaskFinished msg) {
        state.onJobTaskFinished(msg);
        if (state.isProcessed(msg)) {
            ActorRef<EulerCommand> replyTo = state.getReplyTo(msg);
            replyTo.tell(new JobItemProcessed(msg.uri, msg.itemURI));
            state.finish(msg);
        }
        return Behaviors.same();
    }

    private void distributeToTasks(JobItemToProcess msg) {
        for (Task task : tasks) {
            JobTaskToProcess jttp = new JobTaskToProcess(msg, getContext().getSelf());
            if (task.accept(jttp)) {
                ActorRef<TaskCommand> taskRef = getOrSpawnTaskRef(task);
                taskRef.tell(jttp);
                Duration taskTimeoutDuration = task.getTimeout();
                if (!taskTimeoutDuration.isNegative() && !taskTimeoutDuration.isZero()) {
                    TaskTimeout runnable = new TaskTimeout(getContext().getSelf(), jttp, task.name(), taskTimeoutDuration);
                    Cancellable cancellable = getContext().getSystem().scheduler().scheduleOnce(taskTimeoutDuration, runnable, getContext().getExecutionContext());
                    state.processingStartedWithTimeout(msg, cancellable);
                }
            }
        }
    }

    private class TaskTimeout implements Runnable {

        private final ActorRef<ProcessorCommand> processorRef;
        private final JobTaskToProcess msg;
        private final String taskName;
        private final Duration timeout;

        private TaskTimeout(ActorRef<ProcessorCommand> processorRef, JobTaskToProcess msg, String taskName, Duration timeout) {
            super();
            this.processorRef = processorRef;
            this.msg = msg;
            this.taskName = taskName;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            getContext().getLog().warn("Task {} timed out after {} ms.", taskName, timeout.toMillis());
            processorRef.tell(new InternalJobTaskFailed(msg, taskName));
        }

    }

    private ActorRef<TaskCommand> getTaskRef(Task task) {
        return mapping.get(task.name());
    }

    private ActorRef<TaskCommand> getOrSpawnTaskRef(Task task) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            Behavior<TaskCommand> behavior = superviseTaskBehavior(task);
            ActorRef<TaskCommand> ref = getContext().spawn(behavior, key);
            return ref;
        });
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.restart());
        return behavior;
    }

    private Behavior<ProcessorCommand> onInternalJobTaskFailed(InternalJobTaskFailed msg) {
        mapping.remove(msg.taskName);
        onJobTaskFailed(msg);
        return Behaviors.same();
    }

    public Behavior<ProcessorCommand> onJobTaskFailed(JobTaskFailed msg) {
        state.onJobTaskFailed(msg);
        if (state.isProcessed(msg)) {
            ActorRef<EulerCommand> replyTo = state.getReplyTo(msg);
            replyTo.tell(new JobItemProcessed(msg.uri, msg.itemURI));
            state.finish(msg);
        }
        return Behaviors.same();
    }

    private static class InternalJobTaskFailed extends JobTaskFailed {

        public final String taskName;

        public InternalJobTaskFailed(JobTaskToProcess msg, String taskName) {
            super(msg, ProcessingContext.EMPTY);
            this.taskName = taskName;
        }

    }
}