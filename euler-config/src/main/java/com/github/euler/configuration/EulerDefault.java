package com.github.euler.configuration;

import java.util.List;

public class EulerDefault implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new PipelineTaskConfigConverter(),
                new ConcurrentTaskConfigConverter(),
                new PooledTaskConfigConverter(),
                new BarrierTaskConfigConverter(),
                new BatchBarrierTaskConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new AlwaysResumeStrategyConfigConverter(),
                new NotifiedResumeStrategyConfigConverter(),
                new DefaultSourceNotificationStrategyConfigConverter());
    }

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return List.of(new SourceNotificationStrategyConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Euler Default Extension";
    }

}
