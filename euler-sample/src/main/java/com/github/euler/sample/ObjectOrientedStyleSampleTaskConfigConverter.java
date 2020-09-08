package com.github.euler.sample;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.sample.ObjectOrientedStyleSampleTask.OOTask;
import com.typesafe.config.Config;

public class ObjectOrientedStyleSampleTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "oo-style-sample-task";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String taskName = config.getString("name");
        return new OOTask(taskName);
    }

}
