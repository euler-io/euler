package com.github.euler.preview;

import java.io.File;
import java.net.URL;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PreviewCacheStorageStrategyConfigConverter extends AbstractStorageStrategyConfigConverter {

    @Override
    public String configType() {
        return "preview-cache";
    }

    @Override
    public StorageStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        File root = new File(config.getString("root"));
        String suffix = config.getString("suffix");
        int width = config.getInt("width");
        int height = config.getInt("height");
        return new PreviewCacheStorageStrategy(root, suffix, width, height);
    }

    private Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("previewcache.conf");
        return ConfigFactory.parseURL(resource);
    }

}
