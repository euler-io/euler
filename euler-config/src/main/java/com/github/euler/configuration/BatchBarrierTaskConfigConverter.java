package com.github.euler.configuration;

import java.net.URL;

import com.github.euler.core.BatchBarrierCondition;
import com.github.euler.core.BatchBarrierTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class BatchBarrierTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "batch-barrier";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        int batchMaxSize = config.getInt("batch-max-size");
        Task task = tasksConfigConverter.convertTask(config.getValue("task"), ctx, typesConfigConverter);
        BatchBarrierCondition condition = typesConfigConverter.convert(AbstractBatchBarrierConditionConfigConverter.CONDITION, config.getValue("condition"), ctx);
        return new BatchBarrierTask(getName(config, tasksConfigConverter), batchMaxSize, task, condition);
    }

    private Config getDefaultConfig() {
        URL resource = BatchBarrierTaskConfigConverter.class.getClassLoader().getResource("batchbarriertask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
