package com.github.euler.configuration;

import com.github.euler.core.ConcurrentTask;
import com.github.euler.core.Task;

public class ConcurrentTaskConfigConverter extends AbstractMultiTaskConfigConverter {

    @Override
    public String type() {
        return "concurrent";
    }

    @Override
    protected Task convert(String name, Task[] tasks) {
        return new ConcurrentTask(name, tasks);
    }

}
