package com.github.euler.message;

import java.net.URI;

public abstract class EvidenceItemMessage extends EvidenceMessage {

    public final URI evidenceItemURI;

    public EvidenceItemMessage(URI evidenceURI, URI evidenceItemURI) {
        super(evidenceURI);
        this.evidenceItemURI = evidenceItemURI;
    }

    public EvidenceItemMessage(EvidenceToDiscover etd, URI itemURI) {
        super(etd.evidenceURI);
        this.evidenceItemURI = itemURI;
    }
}
