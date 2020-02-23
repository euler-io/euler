package com.github.euler.testing;

import com.github.euler.core.JobTaskFailed;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class WillFailExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(ProcessingContext ctx) {
        return Behaviors.setup((context) -> new WillFailExecution(context, ctx));
    }

    private final ProcessingContext ctx;

    private WillFailExecution(ActorContext<TaskCommand> context, ProcessingContext ctx) {
        super(context);
        this.ctx = ctx;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, (msg) -> {
            msg.replyTo.tell(new JobTaskFailed(msg, ctx));
            return Behaviors.same();
        });
        return builder.build();
    }

}
