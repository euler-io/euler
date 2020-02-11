package com.github.euler.message;

import akka.actor.typed.ActorRef;

public class EvidenceDiscoveryFailed extends EvidenceDiscovery {

    public EvidenceDiscoveryFailed(EvidenceToDiscover etp, ActorRef<EvidenceMessage> sender) {
        super(etp, sender);
    }

    public EvidenceDiscoveryFailed(EvidenceToDiscover etd) {
        super(etd, etd.sender);
    }

}
