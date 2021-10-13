package com.github.euler.dlj;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class MaxProbDetectedObjectsSerializerConfigConverter extends AbstractDetectedObjectsSerializerConfigConverter {

    @Override
    public String configType() {
        return "max-prob";
    }

    @Override
    public DetectedObjectsSerializer convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new MaxProbDetectedObjectsSerializer();
    }

}
