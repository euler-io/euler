package com.github.euler.common;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class CommonExtension implements EulerExtension {

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new StreamFactoryContextConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return Arrays.asList(new DateOrSizeModificationConditionConfigConverter(),
                new PropertyEqualsConditionConfigConverter(),
                new URIHashIdCalculatorConfigCalculator());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(new IdCalculatorTaskConfigConverter());
    }

}
