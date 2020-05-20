package com.github.euler.file;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class BasicFilePropertiesTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "basic-file-properties";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        return new BasicFilePropertiesTask(name);
    }

}
