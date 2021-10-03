package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.dl4j.AbstractMatrixSerializer.Operator;
import com.typesafe.config.Config;

public class SingleLabelMatrixSerializerConfigConverter extends AbstractMatrixSerializerConfigConverter<String> {

    @Override
    public String configType() {
        return "label";
    }

    @Override
    public MatrixSerializer<String> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String label = config.getString("label");
        float limitValue = config.getNumber("limit").floatValue();
        Operator operator = config.getEnum(Operator.class, "operator");
        int[] indices = config.getIntList("indices").stream().mapToInt(i -> i).toArray();
        return new SingleLabelMatrixSerializer(label, limitValue, operator, indices);
    }

}
