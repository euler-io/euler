package com.github.euler.tika;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class TikaExtension implements EulerExtension {

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new TikaContextConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return EulerExtension.super.typeConverters();
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new CategoryDetectTaskConfigConverter(),
                new ParseTaskConfigConverter());
    }

}
