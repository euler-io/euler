package com.github.euler.tika.embedded;

import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.tika.EmbeddedNamingStrategy;

public abstract class AbstractEmbeddedNamingStrategyConfigConverter implements TypeConfigConverter<EmbeddedNamingStrategy> {

    public static final String TYPE = "embedded-naming-strategy";

    public AbstractEmbeddedNamingStrategyConfigConverter() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}