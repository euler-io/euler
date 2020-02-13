package com.github.euler.core;

import com.github.euler.message.EvidenceItemToProcess;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class FowardingTask implements Task {

    private ActorRef<EvidenceItemToProcess> ref;

    public FowardingTask(ActorRef<EvidenceItemToProcess> ref) {
        this.ref = ref;
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public Behavior<EvidenceItemToProcess> behavior() {
        return Behaviors.receive(EvidenceItemToProcess.class).onAnyMessage((msg) -> {
            ref.tell(msg);
            return Behaviors.same();
        }).build();
    }

}
