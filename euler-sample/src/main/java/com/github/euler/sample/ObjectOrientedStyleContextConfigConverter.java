package com.github.euler.sample;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ConfigContext.Builder;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.ConfigValue;

public class ObjectOrientedStyleContextConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "sample-path";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typesConfigConverter) {
        Builder builder = ConfigContext.builder();
        builder.put("some", "context");
        return builder.build();
    }

}
