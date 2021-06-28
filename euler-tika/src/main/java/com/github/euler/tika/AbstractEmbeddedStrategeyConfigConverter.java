package com.github.euler.tika;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractEmbeddedStrategeyConfigConverter implements TypeConfigConverter<EmbeddedStrategy> {

    public static final String TYPE = "embedded-strategy";

    public AbstractEmbeddedStrategeyConfigConverter() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}