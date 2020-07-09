package com.github.euler.configuration;

import com.typesafe.config.ConfigValue;

public interface ContextConfigConverter {

    public String path();

    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typesConfigConverter);

    default String getDescription() {
        return "";
    }

}
