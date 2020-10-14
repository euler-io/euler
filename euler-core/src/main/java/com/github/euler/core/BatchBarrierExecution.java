package com.github.euler.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import akka.actor.typed.javadsl.StashBuffer;

public class BatchBarrierExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Task task, int limit, BatchBarrierCondition condition) {
        return Behaviors.withStash(limit, stash -> Behaviors.setup(ctx -> new BatchBarrierExecution(ctx, task, condition, stash)));
    }

    private final Task task;
    private final BatchBarrierCondition condition;
    private final StashBuffer<TaskCommand> stash;

    private ActorRef<TaskCommand> taskRef;
    private List<JobTaskToProcess> buffer;

    private BatchBarrierExecution(ActorContext<TaskCommand> context, Task task, BatchBarrierCondition condition, StashBuffer<TaskCommand> stash) {
        super(context);
        this.task = task;
        this.condition = condition;
        this.stash = stash;
        this.buffer = new ArrayList<>();
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(Flush.class, this::onFlush);
        return builder.build();
    }

    public Behavior<TaskCommand> onFlush(Flush msg) {
        if (this.taskRef != null) {
            this.taskRef.tell(msg);
        }
        flush();
        return Behaviors.same();
    }

    private void flush() {
        if (!this.buffer.isEmpty()) {
            List<JobTaskToProcess> msgs = new ArrayList<>(this.buffer);
            List<Boolean> blocked = condition.block(msgs);
            for (int i = 0; i < blocked.size(); i++) {
                JobTaskToProcess msg = msgs.get(i);
                if (blocked.get(i)) {
                    ProcessingContext ctx = ProcessingContext.builder().context("blocked-by", condition.getClass().getName()).build();
                    msg.replyTo.tell(new JobTaskFinished(msg, ctx));
                } else {
                    getTaskRef().tell(msg);
                }
            }
        }
        stash.unstashAll(active());
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {
        stash.stash(msg);
        if (stash.isFull()) {
            buffer.clear();
            stash.unstashAll(active());
        }
        return Behaviors.same();
    }

    public Receive<TaskCommand> active() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcessUnstashed);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcessUnstashed(JobTaskToProcess msg) throws IOException {
        buffer.add(msg);
        return Behaviors.same();
    }

    private ActorRef<TaskCommand> getTaskRef() {
        if (this.taskRef == null) {
            this.taskRef = getContext().spawn(task.behavior(), task.name());
        }
        return this.taskRef;
    }

}
