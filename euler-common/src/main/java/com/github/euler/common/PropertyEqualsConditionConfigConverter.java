package com.github.euler.common;

import com.github.euler.configuration.AbstractBarrierConditionConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.BarrierCondition;
import com.typesafe.config.Config;

public class PropertyEqualsConditionConfigConverter extends AbstractBarrierConditionConfigConverter {

    @Override
    public String configType() {
        return "property-equals";
    }

    @Override
    public BarrierCondition convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String property = config.getString("property");
        return new PropertyEqualsCondition(property);
    }

}
