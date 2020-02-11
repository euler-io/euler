package com.github.euler.message;

import java.net.URI;

public class EvidenceItemFound extends EvidenceItemMessage {

    public EvidenceItemFound(URI evidenceURI, URI evidenceItemURI) {
        super(evidenceURI, evidenceItemURI);
    }

    public EvidenceItemFound(EvidenceToDiscover etd, URI itemURI) {
        super(etd, itemURI);
    }

}
