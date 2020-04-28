package com.github.euler.config;

import com.github.euler.core.ConcurrentTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class ConcurrentTaskCreator extends AbstractMultiTaskTaskCreator {

    @Override
    public String type() {
        return "concurrent";
    }

    @Override
    protected Task createTask(Config config, String name, Task[] tasks) {
        return new ConcurrentTask(name, tasks);
    }

}
