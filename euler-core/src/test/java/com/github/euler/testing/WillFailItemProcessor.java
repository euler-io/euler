package com.github.euler.testing;

import java.io.IOException;

import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class WillFailItemProcessor implements ItemProcessor {

    @Override
    public ProcessingContext process(Item item) throws IOException {
        throw new RuntimeException("I am expected to fail.");
    }

}
