package com.github.euler.file;

import com.github.euler.config.SourceCreator;
import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class FileSourceCreator implements SourceCreator {

    @Override
    public String type() {
        return "file";
    }

    @Override
    public Behavior<SourceCommand> create(Config config) {
        return FileSource.create();
    }

}
