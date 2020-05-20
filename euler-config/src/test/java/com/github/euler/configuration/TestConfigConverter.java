package com.github.euler.configuration;

import com.github.euler.core.Task;
import com.github.euler.core.Tasks;
import com.typesafe.config.Config;

public class TestConfigConverter implements TaskConfigConverter {

    @Override
    public String type() {
        return "test";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typedConfigConverter, TasksConfigConverter tasksConfigConverter) {
        return Tasks.empty(tasksConfigConverter.createTaskName());
    }

}
