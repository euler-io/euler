package com.github.euler.preview;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ImagePreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "image";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        ScalrConfig scalrConfig = PreviewUtils.fromConfig(config);
        return new ImagePreviewGenerator(scalrConfig);
    }

    protected Config getDefaultConfig() {
        URL resource = ImagePreviewGeneratorConfigConverter.class.getClassLoader().getResource("imagepreviewgenerator.conf");
        return ConfigFactory.parseURL(resource);
    }

}
