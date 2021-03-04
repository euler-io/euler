package com.github.euler.opencv;

import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.opencv.operation.ChangeCvtOperationConfigConverter;
import com.github.euler.opencv.operation.CombineOperationsConfigConverter;
import com.github.euler.opencv.operation.EqualizeHistOperationConfigConverter;

public class OpenCVExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(new ChangeCvtOperationConfigConverter(),
                new EqualizeHistOperationConfigConverter(),
                new CombineOperationsConfigConverter(),
                new BooleanMatOfRectSerializerConfigConverter(),
                new ListOfRectsSerializerConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(new CascadeClassifierTaskConfigConverter());
    }

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return List.of(new OpenCVConfigConverter());
    }

    @Override
    public String getDescription() {
        return "OpenCV extension";
    }

}
