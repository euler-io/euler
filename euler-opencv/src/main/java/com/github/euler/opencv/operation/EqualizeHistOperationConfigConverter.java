package com.github.euler.opencv.operation;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.opencv.AbstractMatOperationTypeConfigConverter;
import com.github.euler.opencv.MatOperation;
import com.typesafe.config.Config;

public class EqualizeHistOperationConfigConverter extends AbstractMatOperationTypeConfigConverter {

    @Override
    public String configType() {
        return "equalize-hist";
    }

    @Override
    public MatOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new EqualizeHistOperation();
    }

}
