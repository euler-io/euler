package com.github.euler.configuration;

import com.typesafe.config.Config;

public interface TypeConfigConverter<T> {

    String type();

    String configType();

    T convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter);

    default String getDescription() {
        return "";
    }

}
