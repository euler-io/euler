package com.github.euler.configuration;

import com.github.euler.core.SourceCommand;

import akka.actor.typed.Behavior;

public abstract class AbstractSourceConfigConverter implements TypeConfigConverter<Behavior<SourceCommand>> {

    @Override
    public String type() {
        return SourceConfigConverter.SOURCE;
    }

}
