package com.github.euler.file;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class FileExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return Arrays.asList(
                new FileSourceConfigConverter(),
                new FileStorageStrategyConfigConverter(),
                new FileStreamFactoryConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(new BasicFilePropertiesTaskConfigConverter());
    }

}
