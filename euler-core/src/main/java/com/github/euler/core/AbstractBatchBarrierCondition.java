package com.github.euler.core;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractBatchBarrierCondition implements BatchBarrierCondition, BarrierCondition {

    @Override
    public boolean block(JobTaskToProcess msg) {
        return block(Arrays.asList(msg).get(0));
    }

    @Override
    public abstract List<Boolean> block(List<JobTaskToProcess> msgs);

}
