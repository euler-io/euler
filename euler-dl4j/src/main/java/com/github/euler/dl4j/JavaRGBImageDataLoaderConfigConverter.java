package com.github.euler.dl4j;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.dl4j.JavaRGBImageDataLoader.InterpolationType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class JavaRGBImageDataLoaderConfigConverter extends AbstractDataLoaderConfigConverter {

    @Override
    public String configType() {
        return "java-rgb-image";
    }

    @Override
    public DataLoader convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        int width = config.getInt("width");
        int height = config.getInt("height");
        InterpolationType interpolationType = config.getEnum(InterpolationType.class, "interpolation");
        return new JavaRGBImageDataLoader(width, height, interpolationType);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = JavaRGBImageDataLoaderConfigConverter.class.getClassLoader().getResource("javargbimageloader.conf");
        return ConfigFactory.parseURL(resource);
    }

}
