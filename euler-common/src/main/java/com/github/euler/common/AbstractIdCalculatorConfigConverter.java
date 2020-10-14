package com.github.euler.common;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractIdCalculatorConfigConverter implements TypeConfigConverter<IdCalculator> {

    public static final String ID_CALCULATOR = "id-calculator";

    @Override
    public String type() {
        return ID_CALCULATOR;
    }

}
