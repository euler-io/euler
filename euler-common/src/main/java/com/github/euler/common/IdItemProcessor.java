package com.github.euler.common;

import java.io.IOException;

import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class IdItemProcessor implements ItemProcessor {

    private final IdCalculator calculator;

    public IdItemProcessor(IdCalculator calculator) {
        super();
        this.calculator = calculator;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        String id = calculator.calculate(item);
        return ProcessingContext.builder()
                .context(CommonContext.ID, id)
                .build();
    }

}
