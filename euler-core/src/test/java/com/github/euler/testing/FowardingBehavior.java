package com.github.euler.testing;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class FowardingBehavior<T> extends AbstractBehavior<T> {

    public static <T> Behavior<T> create(ActorRef<T> ref) {
        return Behaviors.setup(ctx -> new FowardingBehavior<T>(ctx, ref));
    }

    private final ActorRef<T> ref;

    public FowardingBehavior(ActorContext<T> ctx, ActorRef<T> ref) {
        super(ctx);
        this.ref = ref;
    }

    @Override
    public Receive<T> createReceive() {
        ReceiveBuilder<T> builder = newReceiveBuilder();
        builder.onAnyMessage((msg) -> {
            ref.tell(msg);
            return Behaviors.same();
        });
        return builder.build();
    }

}
