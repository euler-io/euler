package com.github.euler.dl4j;

import java.net.URL;

import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class ImageScalerDataPreparationConfigConverter extends AbstractDataPreparationConfigConverter {

    @Override
    public String configType() {
        return "image-scaler";
    }

    @Override
    public DataPreparation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        double minRange = config.getDouble("min-range");
        double maxRange = config.getDouble("max-range");
        int maxBits = config.getInt("max-bits");
        ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(minRange, maxRange, maxBits);
        return new ImageScalerDataPreparation(scaler);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = ImageScalerDataPreparationConfigConverter.class.getClassLoader().getResource("imagescaler.conf");
        return ConfigFactory.parseURL(resource);
    }

}
