package com.github.euler.sample;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class ObjectOrientedStyleSampleTypeConfigConverter implements TypeConfigConverter<Object> {

    @Override
    public String type() {
        return "source";
    }

    @Override
    public String configType() {
        return "sample-source";
    }

    @Override
    public Object convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return "some-configurable-object";
    }

}
