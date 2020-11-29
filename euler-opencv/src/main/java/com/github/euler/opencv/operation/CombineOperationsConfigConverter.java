package com.github.euler.opencv.operation;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.opencv.AbstractMatOperationTypeConfigConverter;
import com.github.euler.opencv.MatOperation;
import com.typesafe.config.Config;

public class CombineOperationsConfigConverter extends AbstractMatOperationTypeConfigConverter {

    @Override
    public String configType() {
        return "combine";
    }

    @Override
    public MatOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        MatOperation[] operations = config.getList("operations").stream()
                .map(c -> {
                    MatOperation op = typeConfigConverter.convert(AbstractMatOperationTypeConfigConverter.MAT_OPERATION, c, configContext);
                    return op;
                })
                .toArray(MatOperation[]::new);
        return new CombineOperations(operations);
    }

}
