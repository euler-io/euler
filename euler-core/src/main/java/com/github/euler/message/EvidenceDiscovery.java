package com.github.euler.message;

import java.net.URI;

import akka.actor.typed.ActorRef;

public abstract class EvidenceDiscovery extends EvidenceMessage {

    public final ActorRef<EvidenceMessage> sender;

    public EvidenceDiscovery(URI evidenceURI, ActorRef<EvidenceMessage> sender) {
        super(evidenceURI);
        this.sender = sender;
    }

    public EvidenceDiscovery(EvidenceToProcess etp, ActorRef<EvidenceMessage> sender) {
        this(etp.evidenceURI, sender);
    }

    public EvidenceDiscovery(EvidenceToDiscover etd, ActorRef<EvidenceMessage> sender) {
        super(etd.evidenceURI);
        this.sender = sender;
    }

}
