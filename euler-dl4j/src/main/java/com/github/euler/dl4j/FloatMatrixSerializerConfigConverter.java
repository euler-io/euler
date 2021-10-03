package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class FloatMatrixSerializerConfigConverter extends AbstractMatrixSerializerConfigConverter<Float> {

    @Override
    public String configType() {
        return "float";
    }

    @Override
    public MatrixSerializer<Float> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new FloatMatrixSerializer();
    }

}
