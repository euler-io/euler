package com.github.euler.message;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class EvidenceItemToProcess extends EvidenceItemMessage {

    public final ActorRef<EvidenceMessage> sender;

    public EvidenceItemToProcess(URI evidenceURI, URI evidenceItemURI, ActorRef<EvidenceMessage> sender) {
        super(evidenceURI, evidenceItemURI);
        this.sender = sender;
    }

    public EvidenceItemToProcess(EvidenceItemFound eif, ActorRef<EvidenceMessage> sender) {
        this(eif.evidenceURI, eif.evidenceItemURI, sender);
    }

}
