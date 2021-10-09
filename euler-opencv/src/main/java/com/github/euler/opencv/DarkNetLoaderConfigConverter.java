package com.github.euler.opencv;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class DarkNetLoaderConfigConverter extends AbstractDnnNetLoaderConfigConverter {

    @Override
    public String configType() {
        return "dark-net";
    }

    @Override
    public DnnNetLoader convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String cfgFile = config.getString("config-path");
        String darknetModel = config.getString("model-path");
        return new DarkNetLoader(cfgFile, darknetModel);
    }

}
