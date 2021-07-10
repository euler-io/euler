package com.github.euler.preview;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class PreviewExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new ImagePreviewGeneratorConfigConverter(),
                new PDFBoxPreviewGeneratorConfigConverter(),
                new JODConverterPreviewGeneratorConfigConverter(),
                new PreviewCacheStorageStrategyConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(new PreviewTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Preview generation extension";
    }

}
