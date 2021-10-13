package com.github.euler.dlj;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;

public class YoloV5ModelLoaderConfigConverter extends AbstractZooModelLoaderConfigConverter<Image, DetectedObjects> {

    @Override
    public String configType() {
        return "yolo-v5";
    }

    @Override
    public ZooModelLoader<Image, DetectedObjects> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        String classesPath = config.getString("classes-path");
        String modelPath = config.getString("model-path");
        float confidenceThreshold = config.getNumber("confidence-threshold").floatValue();
        int width = config.getInt("width");
        int height = config.getInt("height");
        return new YoloV5ModelLoader(classesPath, modelPath, confidenceThreshold, width, height);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = YoloV5ModelLoaderConfigConverter.class.getClassLoader().getResource("yolov5model.conf");
        return ConfigFactory.parseURL(resource);
    }

}
