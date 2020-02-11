package com.github.euler.message;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class EvidenceToProcess extends EvidenceMessage {

    public final ActorRef<EvidenceMessage> sender;

    public EvidenceToProcess(URI evidenceURI, ActorRef<EvidenceMessage> sender) {
        super(evidenceURI);
        this.sender = sender;
    }

}
