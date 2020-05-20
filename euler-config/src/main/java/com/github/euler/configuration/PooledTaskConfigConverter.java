package com.github.euler.configuration;

import com.github.euler.core.PooledTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class PooledTaskConfigConverter extends AbstractTaskConfigConverter {

    private static final String TASK = "task";
    private static final String SIZE = "size";

    @Override
    public String type() {
        return "pool";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        int size = config.getInt(SIZE);
        ConfigValue taskConfig = config.getValue(TASK);
        Task task = tasksConfigConverter.convertTask(taskConfig, ctx, typeConfigConverter);
        return new PooledTask(name, size, task);
    }

}
