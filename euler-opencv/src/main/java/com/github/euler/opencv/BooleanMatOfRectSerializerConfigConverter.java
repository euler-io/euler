package com.github.euler.opencv;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class BooleanMatOfRectSerializerConfigConverter extends AbstractMatOfRectSerializerConfigConverter {

    @Override
    public String configType() {
        return "boolean";
    }

    @Override
    public MatOfRectSerializer convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new BooleanMatOfRectSerializer();
    }

}
