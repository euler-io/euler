package com.github.euler.configuration;

import com.typesafe.config.ConfigValue;

public class SourceConfigConverter implements ContextConfigConverter {

    public static final String SOURCE = "source";

    @Override
    public String path() {
        return SOURCE;
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return ConfigContext.builder()
                .put(SOURCE, typeConfigConverter.convert(SOURCE, value, configContext))
                .build();
    }

}
