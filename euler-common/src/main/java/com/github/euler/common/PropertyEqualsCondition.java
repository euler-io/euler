package com.github.euler.common;

import java.util.Objects;

import com.github.euler.core.AbstractBarrierCondition;
import com.github.euler.core.ProcessingContext;

public class PropertyEqualsCondition extends AbstractBarrierCondition {

    private final String property;

    public PropertyEqualsCondition(String property) {
        super();
        this.property = property;
    }

    @Override
    protected boolean block(ProcessingContext ctx) {
        Object contextProp = ctx.context(property);
        Object metadataProp = ctx.metadata(property);

        Objects.requireNonNull(contextProp, () -> property + " not found in context.");
        Objects.requireNonNull(metadataProp, () -> property + " not found in metadata.");

        return contextProp.equals(metadataProp);
    }

}
