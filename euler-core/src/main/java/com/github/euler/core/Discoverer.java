package com.github.euler.core;

import java.net.URI;

import com.github.euler.message.EvidenceToDiscover;

import akka.actor.typed.Behavior;

public interface Discoverer {

    Behavior<EvidenceToDiscover> behavior();

    String name();

    boolean accepts(URI evidenceURI);

}
