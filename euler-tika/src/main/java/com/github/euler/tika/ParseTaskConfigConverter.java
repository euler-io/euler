package com.github.euler.tika;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class ParseTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "parse";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);
        StorageStrategy parsedContentStrategy = typeConfigConverter.convert("storage-strategy", config.getValue("parsed-storage-strategy"), ctx);
        StorageStrategy embeddedContentStrategy = typeConfigConverter.convert("storage-strategy", config.getValue("embedded-storage-strategy"), ctx);
        return ParseTask.builder(name, streamFactory, parsedContentStrategy, embeddedContentStrategy).build();
    }

}
