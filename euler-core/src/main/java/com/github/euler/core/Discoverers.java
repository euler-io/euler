package com.github.euler.core;

import java.net.URI;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.euler.message.EvidenceDiscoveryFinished;
import com.github.euler.message.EvidenceItemFound;
import com.github.euler.message.EvidenceToDiscover;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Discoverers {

    private Discoverers() {
        super();
    }

    public static Discoverer setup(final String name, final Predicate<URI> accept, final Supplier<Behavior<EvidenceToDiscover>> behavior) {
        return new Discoverer() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Behavior<EvidenceToDiscover> behavior() {
                return behavior.get();
            }

            @Override
            public boolean accepts(URI evidenceURI) {
                return accept.test(evidenceURI);
            }

        };
    }

    public static Discoverer acceptAll(final Supplier<Behavior<EvidenceToDiscover>> behavior) {
        return setup("accept-all", (uri) -> true, behavior);
    }

    public static Discoverer acceptAll() {
        return acceptAll(() -> emptyBehavior());
    }

    public static Discoverer acceptNone(final Supplier<Behavior<EvidenceToDiscover>> behavior) {
        return setup("accept-all", (uri) -> false, behavior);
    }

    public static Discoverer acceptNone() {
        return acceptNone(() -> emptyBehavior());
    }

    public static Discoverer empty() {
        return setup("empty", (u) -> true, () -> emptyBehavior());
    }

    public static Behavior<EvidenceToDiscover> emptyBehavior() {
        return Behaviors.receive(EvidenceToDiscover.class)
                .onMessage(EvidenceToDiscover.class, (msg) -> {
                    msg.sender.tell(new EvidenceDiscoveryFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Discoverer fixed(URI itemURI) {
        return setup("fixed", (u) -> true, () -> fixedItemBehavior(itemURI));
    }

    public static Behavior<EvidenceToDiscover> fixedItemBehavior(URI itemURI) {
        return Behaviors.receive(EvidenceToDiscover.class)
                .onMessage(EvidenceToDiscover.class, (msg) -> {
                    msg.sender.tell(new EvidenceItemFound(msg, itemURI));
                    msg.sender.tell(new EvidenceDiscoveryFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Discoverer foward(ActorRef<EvidenceToDiscover> ref) {
        return acceptAll(() -> fowardBehavior(ref));
    }

    public static Behavior<EvidenceToDiscover> fowardBehavior(ActorRef<EvidenceToDiscover> ref) {
        return Behaviors.receive(EvidenceToDiscover.class)
                .onMessage(EvidenceToDiscover.class, (msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }

}
