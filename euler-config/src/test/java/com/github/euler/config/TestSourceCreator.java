package com.github.euler.config;

import com.github.euler.core.SourceCommand;
import com.github.euler.core.Sources;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class TestSourceCreator implements SourceCreator {

    @Override
    public String type() {
        return "test";
    }

    @Override
    public Behavior<SourceCommand> create(Config config) {
        return Sources.emptyBehavior();
    }

}
