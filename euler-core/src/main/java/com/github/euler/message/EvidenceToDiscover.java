package com.github.euler.message;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class EvidenceToDiscover extends EvidenceDiscovery {

    public EvidenceToDiscover(EvidenceToProcess etp, ActorRef<EvidenceMessage> sender) {
        super(etp, sender);
    }

    public EvidenceToDiscover(URI evidenceURI, ActorRef<EvidenceMessage> sender) {
        super(evidenceURI, sender);
    }

}
