package com.github.euler.testing;

import java.time.Duration;

import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class DelayedExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Duration delay) {
        return Behaviors.setup(ctx -> new DelayedExecution(ctx, delay));
    }

    private final Duration delay;

    public DelayedExecution(ActorContext<TaskCommand> context, Duration delay) {
        super(context);
        this.delay = delay;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, (msg) -> {
            Thread.sleep(delay.toMillis());
            msg.replyTo.tell(new JobTaskFinished(msg, ProcessingContext.EMPTY));
            return Behaviors.same();
        });
        return builder.build();
    }

}
