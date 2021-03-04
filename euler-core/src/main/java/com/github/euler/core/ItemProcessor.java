package com.github.euler.core;

import java.io.IOException;

public interface ItemProcessor {

    public static final ItemProcessor VOID = new ItemProcessor() {

        @Override
        public ProcessingContext process(Item item) throws IOException {
            return ProcessingContext.EMPTY;
        }
    };

    ProcessingContext process(Item item) throws IOException;

}