package com.github.euler.file;

import java.net.URL;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class BasicFilePropertiesTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "basic-file-properties";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);
        return new BasicFilePropertiesTask(name);
    }

    protected Config getDefaultConfig() {
        URL resource = BasicFilePropertiesTaskConfigConverter.class.getClassLoader().getResource("basicfilepropertiestask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
