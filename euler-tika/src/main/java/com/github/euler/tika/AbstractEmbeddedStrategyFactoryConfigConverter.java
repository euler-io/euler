package com.github.euler.tika;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractEmbeddedStrategyFactoryConfigConverter implements TypeConfigConverter<EmbeddedStrategyFactory> {

    public static final String TYPE = "embedded-strategy";

    public AbstractEmbeddedStrategyFactoryConfigConverter() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}