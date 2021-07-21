package com.github.euler.common;

import java.io.IOException;

import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class ContextItemProcessor implements ItemProcessor {

    private final ProcessingContext context;

    public ContextItemProcessor(ProcessingContext context) {
        super();
        this.context = context;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        return context;
    }

}
