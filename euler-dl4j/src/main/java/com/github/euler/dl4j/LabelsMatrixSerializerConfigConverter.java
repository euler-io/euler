package com.github.euler.dl4j;

import java.util.List;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.dl4j.AbstractMatrixSerializer.Operator;
import com.typesafe.config.Config;

public class LabelsMatrixSerializerConfigConverter extends AbstractMatrixSerializerConfigConverter<List<String>> {

    @Override
    public String configType() {
        return "labels";
    }

    @Override
    public MatrixSerializer<List<String>> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        List<String> labels = config.getStringList("labels");
        int rowIndex = config.getInt("row");
        float limitValue = config.getNumber("limit").floatValue();
        Operator operator = config.getEnum(Operator.class, "operator");
        return new LabelsMatrixSerializer(labels, rowIndex, limitValue, operator);
    }

}
