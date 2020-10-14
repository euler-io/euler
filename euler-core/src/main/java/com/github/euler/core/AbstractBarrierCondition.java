package com.github.euler.core;

public abstract class AbstractBarrierCondition implements BarrierCondition {

    @Override
    public boolean block(JobTaskToProcess msg) {
        return block(msg.ctx);
    }

    protected abstract boolean block(ProcessingContext ctx);

}
