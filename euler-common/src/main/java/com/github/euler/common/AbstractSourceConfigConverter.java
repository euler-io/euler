package com.github.euler.common;

import com.github.euler.configuration.SourceConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.core.SourceCommand;

import akka.actor.typed.Behavior;

public abstract class AbstractSourceConfigConverter implements TypeConfigConverter<Behavior<SourceCommand>> {

    @Override
    public String type() {
        return SourceConfigConverter.SOURCE;
    }

}
