package com.github.euler.configuration;

import com.typesafe.config.Config;

public abstract class AbstractTaskConfigConverter implements TaskConfigConverter {

    private static final String NAME = "name";

    protected String getName(Config config, TasksConfigConverter tasksConfigConverter) {
        if (config.hasPath(NAME)) {
            return config.getString(NAME);
        } else {
            return tasksConfigConverter.createTaskName();
        }
    }

}
