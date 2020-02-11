package com.github.euler.testing;

import com.github.euler.message.EvidenceToDiscover;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class WillFailDiscovererBehavior extends AbstractBehavior<EvidenceToDiscover> {

    public static Behavior<EvidenceToDiscover> create() {
        return Behaviors.setup(ctx -> new WillFailDiscovererBehavior(ctx));
    }

    public WillFailDiscovererBehavior(ActorContext<EvidenceToDiscover> context) {
        super(context);
    }

    @Override
    public Receive<EvidenceToDiscover> createReceive() {
        ReceiveBuilder<EvidenceToDiscover> builder = newReceiveBuilder();
        builder.onMessage(EvidenceToDiscover.class, this::onEvidenceToDiscover);
        return builder.build();
    }

    private Behavior<EvidenceToDiscover> onEvidenceToDiscover(EvidenceToDiscover etd) {
        throw new RuntimeException("I am expected to fail.");
    }

}
