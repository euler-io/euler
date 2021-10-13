package com.github.euler.dlj;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class DljExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new YoloV5ModelLoaderConfigConverter(),
                new ListDetectedObjectsSerializerConfigConverter(),
                new MaxProbDetectedObjectsSerializerConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new ObjectDetectionTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "DLJ Extension";
    }

}
