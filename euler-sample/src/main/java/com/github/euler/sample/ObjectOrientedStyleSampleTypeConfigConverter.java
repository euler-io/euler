package com.github.euler.sample;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.SourceExecution;
import com.github.euler.sample.ObjectOrientedStyleSampleSource.OOSource;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class ObjectOrientedStyleSampleTypeConfigConverter implements TypeConfigConverter<Behavior<SourceCommand>> {

    @Override
    public String type() {
        return "source";
    }

    @Override
    public String configType() {
        return "sample-source";
    }

    @Override
    public Behavior<SourceCommand> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return SourceExecution.create(new OOSource());
    }

}
