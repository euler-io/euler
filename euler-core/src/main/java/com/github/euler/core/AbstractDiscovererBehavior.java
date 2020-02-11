package com.github.euler.core;

import com.github.euler.message.EvidenceToDiscover;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;

public abstract class AbstractDiscovererBehavior extends AbstractBehavior<EvidenceToDiscover> {

    public AbstractDiscovererBehavior(ActorContext<EvidenceToDiscover> context) {
        super(context);
    }

}
