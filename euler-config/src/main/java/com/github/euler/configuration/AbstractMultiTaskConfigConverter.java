package com.github.euler.configuration;

import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;

public abstract class AbstractMultiTaskConfigConverter extends AbstractTaskConfigConverter {

    private static final String TASKS = "tasks";

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        Task[] tasks = getTasks(config, ctx, typeConfigConverter, tasksConfigConverter);
        return convert(name, tasks);
    }

    private Task[] getTasks(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        ConfigList configList = config.getList(TASKS);
        return tasksConfigConverter.convert(configList, ctx, typeConfigConverter)
                .stream()
                .toArray(s -> new Task[s]);
    }

    protected abstract Task convert(String name, Task[] tasks);

}
