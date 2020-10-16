package com.github.euler.testing;

import java.io.IOException;

import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class WillFailItemProcessor implements ItemProcessor {

    private int maxFails;
    private int failed = 0;

    public WillFailItemProcessor(int maxFails) {
        super();
        this.maxFails = maxFails;
    }

    public WillFailItemProcessor() {
        this(-1);
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        if (failed < maxFails || maxFails < 0) {
            failed++;
            throw new RuntimeException("I am expected to fail.");
        } else {
            return ProcessingContext.EMPTY;
        }
    }

}
