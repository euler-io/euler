package com.github.euler.config;

import com.github.euler.core.Task;
import com.github.euler.core.Tasks;
import com.typesafe.config.Config;

public class TestTaskCreator implements TaskCreator {

    @Override
    public String type() {
        return "test";
    }

    @Override
    public Task create(Config config, TaskFactory taskFactory, ConfigContext ctx) {
        return Tasks.empty("test");
    }

}
