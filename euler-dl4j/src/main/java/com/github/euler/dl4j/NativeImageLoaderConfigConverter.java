package com.github.euler.dl4j;

import java.net.URL;

import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.loader.NativeImageLoader;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class NativeImageLoaderConfigConverter implements TypeConfigConverter<BaseImageLoader> {

    private static final String TYPE = "native-image-loader";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String configType() {
        return TYPE;
    }

    @Override
    public BaseImageLoader convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        long height = config.getLong("height");
        long width = config.getLong("width");
        long channels = config.getLong("channels");
        return new NativeImageLoader(height, width, channels);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = NativeImageLoaderConfigConverter.class.getClassLoader().getResource("nativeimageloader.conf");
        return ConfigFactory.parseURL(resource);
    }

}
