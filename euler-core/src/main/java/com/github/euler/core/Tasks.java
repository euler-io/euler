package com.github.euler.core;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.euler.message.EvidenceItemToProcess;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Tasks {

    private Tasks() {
        super();
    }

    public static Task setup(final String name, final Predicate<EvidenceItemToProcess> accept, final Supplier<Behavior<EvidenceItemToProcess>> behavior) {
        return new Task() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean accept(EvidenceItemToProcess msg) {
                return accept.test(msg);
            }

            @Override
            public Behavior<EvidenceItemToProcess> behavior() {
                return behavior.get();
            }

        };
    }

    public static Task accept(String name, final Supplier<Behavior<EvidenceItemToProcess>> behavior) {
        return setup(name, (msg) -> true, behavior);
    }

    public static Task accept(String name) {
        return accept(name, () -> voidBehavior());
    }

    public static Task notAccept(String name, final Supplier<Behavior<EvidenceItemToProcess>> behavior) {
        return setup(name, (msg) -> false, behavior);
    }

    public static Task notAccept(String name) {
        return notAccept(name, () -> voidBehavior());
    }

    public static Task foward(String name, ActorRef<EvidenceItemToProcess> ref) {
        return setup(name, (msg) -> true, () -> fowardBehavior(ref));
    }

    public static Behavior<EvidenceItemToProcess> voidBehavior() {
        return Behaviors.receive(EvidenceItemToProcess.class)
                .onMessage(EvidenceItemToProcess.class, (msg) -> Behaviors.same())
                .build();
    }

    public static Behavior<EvidenceItemToProcess> fowardBehavior(ActorRef<EvidenceItemToProcess> ref) {
        return Behaviors.receive(EvidenceItemToProcess.class)
                .onAnyMessage((msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }
}
