package com.github.euler.core;

import java.net.URI;

import com.github.euler.command.DiscovererCommand;

import akka.actor.typed.Behavior;

public interface Discoverer {

    Behavior<DiscovererCommand> behavior();

    String name();

    boolean accepts(URI evidenceURI);

}
