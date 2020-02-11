package com.github.euler.message;

import java.net.URI;

public class EvidenceDiscoveryFinished extends EvidenceMessage {

    public EvidenceDiscoveryFinished(EvidenceMessage msg) {
        super(msg);
    }

    public EvidenceDiscoveryFinished(URI evidenceURI) {
        super(evidenceURI);
    }

}
