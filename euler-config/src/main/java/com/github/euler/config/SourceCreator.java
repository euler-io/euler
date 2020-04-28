package com.github.euler.config;

import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public interface SourceCreator {

    String type();

    Behavior<SourceCommand> create(Config config);

}
