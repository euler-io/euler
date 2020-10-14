package com.github.euler.configuration;

import com.github.euler.core.BatchBarrierCondition;

public abstract class AbstractBatchBarrierConditionConfigConverter implements TypeConfigConverter<BatchBarrierCondition> {

    public static final String CONDITION = "batch-condition";

    @Override
    public String type() {
        return CONDITION;
    }

}
