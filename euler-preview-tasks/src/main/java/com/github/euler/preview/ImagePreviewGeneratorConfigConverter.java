package com.github.euler.preview;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class ImagePreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "image";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        ScalrConfig scalrConfig = PreviewUtils.fromConfig(config);
        return new ImagePreviewGenerator(scalrConfig);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = ImagePreviewGeneratorConfigConverter.class.getClassLoader().getResource("imagepreviewgenerator.conf");
        return ConfigFactory.parseURL(resource);
    }

}
