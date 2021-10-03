package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.dl4j.AbstractMatrixSerializer.Operator;
import com.typesafe.config.Config;

public class BooleanMatrixSerializerConfigConverter extends AbstractMatrixSerializerConfigConverter<Boolean> {

    @Override
    public String configType() {
        return "boolean";
    }

    @Override
    public MatrixSerializer<Boolean> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        float limitValue = config.getNumber("limit").floatValue();
        Operator operator = config.getEnum(Operator.class, "operator");
        int[] indices = config.getIntList("indices").stream().mapToInt(i -> i).toArray();
        return new BooleanMatrixSerializer(limitValue, operator, indices);
    }

}
