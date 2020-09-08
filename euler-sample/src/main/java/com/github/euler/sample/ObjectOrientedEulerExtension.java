package com.github.euler.sample;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;

public class ObjectOrientedEulerExtension implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(new ObjectOrientedStyleSampleTaskConfigConverter());
    }

}
