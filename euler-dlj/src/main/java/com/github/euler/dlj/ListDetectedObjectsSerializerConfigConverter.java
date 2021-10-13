package com.github.euler.dlj;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class ListDetectedObjectsSerializerConfigConverter extends AbstractDetectedObjectsSerializerConfigConverter {

    @Override
    public String configType() {
        return "list";
    }

    @Override
    public DetectedObjectsSerializer convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new ListDetectedObjectsSerializer();
    }

}
