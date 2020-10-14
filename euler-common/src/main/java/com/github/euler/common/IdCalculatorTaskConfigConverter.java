package com.github.euler.common;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.ItemProcessorTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class IdCalculatorTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "id-calculator";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        IdCalculator calculator = typeConfigConverter.convert(AbstractIdCalculatorConfigConverter.ID_CALCULATOR, config.getValue("calculator"), ctx);
        return new ItemProcessorTask(name, new IdItemProcessor(calculator));
    }

}
