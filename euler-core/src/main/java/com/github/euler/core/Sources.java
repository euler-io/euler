package com.github.euler.core;

import java.net.URI;
import java.util.function.Predicate;
import java.util.function.Supplier;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Sources {

    private Sources() {
        super();
    }

    public static Source setup(final String name, final Predicate<URI> accept, final Supplier<Behavior<SourceCommand>> behavior) {
        return new Source() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Behavior<SourceCommand> behavior() {
                return behavior.get();
            }

            @Override
            public boolean accepts(URI evidenceURI) {
                return accept.test(evidenceURI);
            }

        };
    }

    public static Source acceptAll(final Supplier<Behavior<SourceCommand>> behavior) {
        return setup("accept-all", (uri) -> true, behavior);
    }

    public static Source acceptAll() {
        return acceptAll(() -> emptyBehavior());
    }

    public static Source acceptNone(final Supplier<Behavior<SourceCommand>> behavior) {
        return setup("accept-all", (uri) -> false, behavior);
    }

    public static Source acceptNone() {
        return acceptNone(() -> emptyBehavior());
    }

    public static Source empty() {
        return setup("empty", (u) -> true, () -> emptyBehavior());
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
        return setup("fixed", (u) -> true, () -> fixedItemBehavior(itemURI));
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
        return acceptAll(() -> fowardBehavior(ref));
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
