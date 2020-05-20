package com.github.euler.configuration;

import com.github.euler.core.PipelineTask;
import com.github.euler.core.Task;

public class PipelineTaskConfigConverter extends AbstractMultiTaskConfigConverter {

    @Override
    public String type() {
        return "pipeline";
    }

    @Override
    protected Task convert(String name, Task[] tasks) {
        return new PipelineTask(name, tasks);
    }

}
