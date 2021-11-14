package com.github.euler.file;

import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class FileExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new FileSourceConfigConverter(),
                new FileStorageStrategyConfigConverter(),
                new FileStreamFactoryConfigConverter(),
                new ExistsInFileSystemConditionConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(new BasicFilePropertiesTaskConfigConverter());
    }

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return List.of(
                new PathDeleteConfigConverter(),
                new PathCreateConfigConveter());
    }

    @Override
    public String getDescription() {
        return "File Extension";
    }

}
