package com.github.euler.testing;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class WillFailBehavior<T> extends AbstractBehavior<T> {

    public static <T> Behavior<T> create() {
        return Behaviors.setup(ctx -> new WillFailBehavior<T>(ctx));
    }

    private WillFailBehavior(ActorContext<T> context) {
        super(context);
    }

    @Override
    public Receive<T> createReceive() {
        ReceiveBuilder<T> builder = newReceiveBuilder();
        builder.onAnyMessage((msg) -> {
            throw new RuntimeException("I am expected to fail.");
        });
        return builder.build();
    }

}
