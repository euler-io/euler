package com.github.euler.configuration;

import com.github.euler.core.BarrierCondition;

public abstract class AbstractBarrierConditionConfigConverter implements TypeConfigConverter<BarrierCondition> {

    public static final String CONDITION = "condition";

    @Override
    public String type() {
        return CONDITION;
    }

}
