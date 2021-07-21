package com.github.euler.common;

import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class ContextTask extends AbstractTask {

    private final ProcessingContext context;

    public ContextTask(String name, ProcessingContext context) {
        super(name);
        this.context = context;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new ContextItemProcessor(context);
    }

}
