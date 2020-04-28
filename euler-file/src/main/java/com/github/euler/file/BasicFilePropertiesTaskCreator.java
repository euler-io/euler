package com.github.euler.file;

import com.github.euler.config.TaskCreator;
import com.github.euler.config.TaskFactory;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class BasicFilePropertiesTaskCreator implements TaskCreator {

    private static final String NAME = "name";

    @Override
    public String type() {
        return "basic-file-properties";
    }

    @Override
    public Task create(Config config, TaskFactory taskFactory) {
        String name;
        if (config.hasPath(NAME)) {
            name = config.getString(NAME);
        } else {
            name = taskFactory.createTaskName();
        }
        return new BasicFilePropertiesTask(name);
    }

}
