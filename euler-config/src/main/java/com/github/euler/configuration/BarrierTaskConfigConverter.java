package com.github.euler.configuration;

import com.github.euler.core.BarrierCondition;
import com.github.euler.core.BarrierTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class BarrierTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "barrier";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        Task task = tasksConfigConverter.convertTask(config.getValue("task"), ctx, typesConfigConverter);
        BarrierCondition condition = typesConfigConverter.convert(AbstractBarrierConditionConfigConverter.CONDITION, config.getValue("condition"), ctx);
        return new BarrierTask(getName(config, tasksConfigConverter), task, condition);
    }

}
