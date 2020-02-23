package com.github.euler.core;

import akka.actor.typed.Behavior;

public interface Source {

    Behavior<SourceCommand> behavior();

    String name();

}
