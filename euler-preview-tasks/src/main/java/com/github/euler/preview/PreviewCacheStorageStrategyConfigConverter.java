package com.github.euler.preview;

import java.io.File;
import java.net.URL;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class PreviewCacheStorageStrategyConfigConverter extends AbstractStorageStrategyConfigConverter {

    @Override
    public String configType() {
        return "preview-cache";
    }

    @Override
    public StorageStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        File root = new File(config.getString("root"));
        String suffix = config.getString("suffix");
        int width = config.getInt("width");
        int height = config.getInt("height");
        String format = config.getString("format");
        return new PreviewCacheStorageStrategy(root, suffix, width, height, format);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    private Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("previewcache.conf");
        return ConfigFactory.parseURL(resource);
    }

}
