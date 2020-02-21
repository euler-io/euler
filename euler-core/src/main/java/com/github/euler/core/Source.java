package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.Behavior;

public interface Source {

    Behavior<SourceCommand> behavior();

    String name();

    boolean accepts(URI evidenceURI);

}
