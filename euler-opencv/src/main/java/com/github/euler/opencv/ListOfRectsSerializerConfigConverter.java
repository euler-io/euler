package com.github.euler.opencv;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class ListOfRectsSerializerConfigConverter extends AbstractMatOfRectSerializerConfigConverter {

    @Override
    public String configType() {
        return "list-of-rects";
    }

    @Override
    public MatOfRectSerializer convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new ListOfRectsSerializer();
    }

}
