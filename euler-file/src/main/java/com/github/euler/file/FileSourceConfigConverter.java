package com.github.euler.file;

import com.github.euler.configuration.AbstractSourceConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class FileSourceConfigConverter extends AbstractSourceConfigConverter {

    @Override
    public String configType() {
        return "file";
    }

    @Override
    public Behavior<SourceCommand> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return FileSource.create();
    }

}
