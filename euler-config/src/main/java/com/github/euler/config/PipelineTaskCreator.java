package com.github.euler.config;

import com.github.euler.core.PipelineTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class PipelineTaskCreator extends AbstractMultiTaskTaskCreator {

    @Override
    public String type() {
        return "pipeline";
    }

    @Override
    protected Task createTask(Config config, String name, Task[] tasks) {
        return new PipelineTask(name, tasks);
    }

}
