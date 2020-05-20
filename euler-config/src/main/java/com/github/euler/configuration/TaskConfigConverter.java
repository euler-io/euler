package com.github.euler.configuration;

import com.github.euler.core.Task;
import com.typesafe.config.Config;

public interface TaskConfigConverter {

    String type();

    Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter);

}
