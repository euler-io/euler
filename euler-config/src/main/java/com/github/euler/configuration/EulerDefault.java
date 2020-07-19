package com.github.euler.configuration;

import java.util.Arrays;
import java.util.List;

public class EulerDefault implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new PipelineTaskConfigConverter(),
                new ConcurrentTaskConfigConverter(),
                new PooledTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Euler Default Extension";
    }

}
