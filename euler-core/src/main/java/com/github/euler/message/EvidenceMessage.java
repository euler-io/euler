package com.github.euler.message;

import java.net.URI;

public abstract class EvidenceMessage {

    public final URI evidenceURI;

    public EvidenceMessage(URI evidenceURI) {
        super();
        this.evidenceURI = evidenceURI;
    }

    public EvidenceMessage(EvidenceMessage msg) {
        this(msg.evidenceURI);
    }

}
