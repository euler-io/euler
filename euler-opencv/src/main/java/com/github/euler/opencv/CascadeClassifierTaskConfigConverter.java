package com.github.euler.opencv;

import org.opencv.objdetect.CascadeClassifier;

import com.github.euler.common.StreamFactory;
import com.github.euler.common.StreamFactoryContextConfigConverter;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.ItemProcessorTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class CascadeClassifierTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "cascade-classifier";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        String name = getName(config, tasksConfigConverter);
        String classifierName = config.getString("classifier-name");
        String classifierPath = config.getString("classifier-path");
        CascadeClassifier classifier = new CascadeClassifier(classifierPath);
        StreamFactory sf = typesConfigConverter.convert(StreamFactoryContextConfigConverter.STREAM_FACTORY, config.getValue("stream-factory"), ctx);
        MatOperation matOperation = typesConfigConverter.convert(AbstractMatOperationTypeConfigConverter.MAT_OPERATION, config.getValue("mat-operation"), ctx);
        MatOfRectSerializer serializer = typesConfigConverter.convert(AbstractMatOfRectSerializerConfigConverter.MAT_OF_RECT_SERIALIZER, config.getValue("serializer"), ctx);
        return new ItemProcessorTask(name, new CascadeClassifierItemProcessor(classifierName, classifier, sf, matOperation, serializer));
    }

}
