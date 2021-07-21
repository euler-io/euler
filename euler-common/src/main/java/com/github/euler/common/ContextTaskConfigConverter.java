package com.github.euler.common;

import java.net.URL;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Builder;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class ContextTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "context";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);

        Builder builder = ProcessingContext.builder();
        config.getConfig("metadata").root().unwrapped().forEach((k, v) -> builder.metadata(k, v));
        config.getConfig("context").root().unwrapped().forEach((k, v) -> builder.context(k, v));
        ProcessingContext context = builder.build();
        return new ContextTask(getName(config, tasksConfigConverter), context);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("contexttask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
