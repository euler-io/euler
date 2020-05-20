package com.github.euler.common;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.ConfigValue;

public class StreamFactoryContextConfigConverter implements ContextConfigConverter {

    public static final String STREAM_FACTORY = "stream-factory";

    @Override
    public String path() {
        return STREAM_FACTORY;
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(STREAM_FACTORY, value, configContext);
    }

}
