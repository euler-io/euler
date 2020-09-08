package com.github.euler.sample;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class EulerSampleExtension implements EulerExtension {

    @Override
    public String getDescription() {
        return "Sample Euler Extension";
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(new ObjectOrientedStyleSampleTaskConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return Arrays.asList(new ObjectOrientedStyleSampleTypeConfigConverter());
    }

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new ObjectOrientedStyleContextConfigConverter());
    }

}
