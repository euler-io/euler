package com.github.euler.file;

import com.github.euler.configuration.AbstractBarrierConditionConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.BarrierCondition;
import com.typesafe.config.Config;

public class ExistsInFileSystemConditionConfigConverter extends AbstractBarrierConditionConfigConverter {

    @Override
    public String configType() {
        return "exists-in-file-system";
    }

    @Override
    public BarrierCondition convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new ExistsInFileSystemCondition();
    }

}
