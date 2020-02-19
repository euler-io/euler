package com.github.euler.core;

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

public class EulerProcessor extends AbstractBehavior<ProcessorCommand> {

    public static Behavior<ProcessorCommand> create(Task... tasks) {
        return Behaviors.setup(ctx -> new EulerProcessor(ctx, tasks));
    }

    private Task[] tasks;
    private Map<String, ActorRef<TaskCommand>> mapping;

    private EulerProcessorState state;

    public EulerProcessor(ActorContext<ProcessorCommand> ctx, Task... tasks) {
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
        return builder.build();
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
        }
        return Behaviors.same();
    }

    private void distributeToTasks(JobItemToProcess msg) {
        for (Task task : tasks) {
            JobTaskToProcess jttp = new JobTaskToProcess(msg, getContext().getSelf());
            if (task.accept(jttp)) {
                ActorRef<TaskCommand> taskRef = getTaskRef(task, msg);
                taskRef.tell(jttp);
            }
        }
    }

    private ActorRef<TaskCommand> getTaskRef(Task task, JobItemToProcess msg) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            Behavior<TaskCommand> behavior = superviseTaskBehavior(task);
            ActorRef<TaskCommand> ref = getContext().spawn(behavior, key);
            getContext().watchWith(ref, new InternalJobTaskFailed(msg, task.name()));
            return ref;
        });
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.stop());
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
        }
        return Behaviors.same();
    }

    private static class InternalJobTaskFailed extends JobTaskFailed {

        public final String taskName;

        public InternalJobTaskFailed(JobItemToProcess msg, String taskName) {
            super(msg);
            this.taskName = taskName;
        }

    }
}