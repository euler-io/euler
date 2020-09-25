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
        StreamFactory sf = typeConfigConverter.convert(STREAM_FACTORY, value, configContext);
        ConfigContext ctx = ConfigContext.builder().put(StreamFactory.class, sf).build();
        return ctx;
    }

}
