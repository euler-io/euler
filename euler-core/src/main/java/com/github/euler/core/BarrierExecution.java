package com.github.euler.core;

import java.io.IOException;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class BarrierExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Task task, BarrierCondition condition) {
        return Behaviors.setup(ctx -> new BarrierExecution(ctx, task, condition));
    }

    private final Task task;
    private final BarrierCondition condition;

    private ActorRef<TaskCommand> taskRef;

    private BarrierExecution(ActorContext<TaskCommand> context, Task task, BarrierCondition condition) {
        super(context);
        this.task = task;
        this.condition = condition;
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
        return Behaviors.same();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {
        if (condition.block(msg)) {
            ProcessingContext ctx = ProcessingContext.builder().context("blocked-by", condition.getClass().getName()).build();
            msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        } else {
            ActorRef<TaskCommand> taskRef = getTaskRef();
            taskRef.tell(msg);
        }
        return Behaviors.same();
    }

    private ActorRef<TaskCommand> getTaskRef() {
        if (this.taskRef == null) {
            this.taskRef = getContext().spawn(task.behavior(), task.name());
        }
        return this.taskRef;
    }

}
