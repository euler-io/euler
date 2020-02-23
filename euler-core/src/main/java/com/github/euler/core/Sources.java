package com.github.euler.core;

import java.net.URI;
import java.util.function.Supplier;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Sources {

    private Sources() {
        super();
    }

    public static Source setup(final String name, final Supplier<Behavior<SourceCommand>> behavior) {
        return new Source() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Behavior<SourceCommand> behavior() {
                return behavior.get();
            }

        };
    }

    public static Source empty() {
        return setup("empty", () -> emptyBehavior());
    }

    public static Behavior<SourceCommand> emptyBehavior() {
        return Behaviors.receive(SourceCommand.class)
                .onMessage(JobToScan.class, (msg) -> {
                    msg.replyTo.tell(new ScanFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Source fixed(URI itemURI) {
        return setup("fixed", () -> fixedItemBehavior(itemURI));
    }

    public static Behavior<SourceCommand> fixedItemBehavior(URI itemURI) {
        return Behaviors.receive(SourceCommand.class)
                .onMessage(JobToScan.class, (msg) -> {
                    msg.replyTo.tell(new JobItemFound(msg.uri, itemURI));
                    msg.replyTo.tell(new ScanFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Source foward(ActorRef<SourceCommand> ref) {
        return setup("foward", () -> fowardBehavior(ref));
    }

    public static Behavior<SourceCommand> fowardBehavior(ActorRef<SourceCommand> ref) {
        return Behaviors.receive(SourceCommand.class)
                .onAnyMessage((msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }

}
