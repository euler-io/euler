package com.github.euler.tika;

import java.net.URL;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ParseTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "parse";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);
        StorageStrategy parsedContentStrategy = typeConfigConverter.convert("storage-strategy", config.getValue("parsed-storage-strategy"), ctx);
        StorageStrategy embeddedContentStrategy = typeConfigConverter.convert("storage-strategy", config.getValue("embedded-storage-strategy"), ctx);
        return ParseTask.builder(name, streamFactory, parsedContentStrategy, embeddedContentStrategy).build();
    }

    protected Config getDefaultConfig() {
        URL resource = ParseTaskConfigConverter.class.getClassLoader().getResource("parsetask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
