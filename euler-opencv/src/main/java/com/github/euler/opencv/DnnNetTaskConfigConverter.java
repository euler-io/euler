package com.github.euler.opencv;

import org.opencv.dnn.Net;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class DnnNetTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "dnn-net";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        float confidenceThreshold = config.getNumber("confidence-threshold").floatValue();
        MatOfRectSerializer serializer = typesConfigConverter.convert(AbstractMatOfRectSerializerConfigConverter.TYPE, config.getValue("serializer"), ctx);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);

        DnnNetLoader loader = typesConfigConverter.convert(AbstractDnnNetLoaderConfigConverter.TYPE, config.getValue("model-loader"), ctx);
        Net dnnNet = loader.load();

        return DnnNetTask.builder()
                .setName(getName(config, tasksConfigConverter))
                .setConfThreshold(confidenceThreshold)
                .setDnnNet(dnnNet)
                .setSerializer(serializer)
                .setSf(sf)
                .build();
    }

}
