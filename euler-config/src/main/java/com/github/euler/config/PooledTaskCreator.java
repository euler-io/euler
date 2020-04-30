package com.github.euler.config;

import com.github.euler.core.PooledTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class PooledTaskCreator implements TaskCreator {

    private static final String TASK = "task";
    private static final String SIZE = "size";
    private static final String NAME = "name";

    @Override
    public String type() {
        return "pool";
    }

    @Override
    public Task create(Config config, TaskFactory taskFactory, ConfigContext ctx) {
        String name;
        if (config.hasPath(NAME)) {
            name = config.getString(NAME);
        } else {
            name = taskFactory.createTaskName();
        }
        int size = config.getInt(SIZE);
        ConfigValue taskConfig = config.getValue(TASK);
        Task task = taskFactory.create(taskConfig);
        return new PooledTask(name, size, task);
    }

}
