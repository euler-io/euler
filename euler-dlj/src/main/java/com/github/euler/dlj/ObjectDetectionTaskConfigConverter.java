package com.github.euler.dlj;

import java.io.IOException;
import java.net.URL;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

public class ObjectDetectionTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "object-detection";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        try {
            config = getConfig(config);
            String name = getName(config, tasksConfigConverter);
            StreamFactory sf = ctx.getRequired(StreamFactory.class);
            ZooModelLoader<Image, DetectedObjects> modelLoader = typesConfigConverter.convert(AbstractZooModelLoaderConfigConverter.TYPE, config.getValue("model"), ctx);
            ZooModel<Image, DetectedObjects> model = modelLoader.load();
            DetectedObjectsSerializer serializer = typesConfigConverter.convert(AbstractDetectedObjectsSerializerConfigConverter.TYPE, config.getValue("serializer"), ctx);
            String field = config.getString("field");
            return new ObjectDetectionTask(name, sf, model, serializer, field);
        } catch (ModelNotFoundException | MalformedModelException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = ObjectDetectionTaskConfigConverter.class.getClassLoader().getResource("objectdetection.conf");
        return ConfigFactory.parseURL(resource);
    }

}
