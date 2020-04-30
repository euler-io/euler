package com.github.euler.config;

import com.github.euler.core.Task;
import com.typesafe.config.Config;

public abstract class AbstractMultiTaskTaskCreator implements TaskCreator {

    private static final String NAME = "name";
    private static final String TASKS = "tasks";

    @Override
    public Task create(Config config, TaskFactory taskFactory, ConfigContext ctx) {
        Task[] tasks = config.getList(TASKS)
                .stream()
                .map(v -> taskFactory.create(v, ctx))
                .toArray(s -> new Task[s]);
        String name;
        if (config.hasPath(NAME)) {
            name = config.getString(NAME);
        } else {
            name = taskFactory.createTaskName();
        }
        return createTask(config, name, tasks);
    }

    protected abstract Task createTask(Config config, String name, Task[] tasks);

}
