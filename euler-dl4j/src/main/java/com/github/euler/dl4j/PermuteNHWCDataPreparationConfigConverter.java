package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class PermuteNHWCDataPreparationConfigConverter extends AbstractDataPreparationConfigConverter {

    @Override
    public String configType() {
        return "permute-to-nhwc";
    }

    @Override
    public DataPreparation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new PermuteNHWCDataPreparation();
    }

}
