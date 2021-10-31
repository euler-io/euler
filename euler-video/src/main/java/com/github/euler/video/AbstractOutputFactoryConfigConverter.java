package com.github.euler.video;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractOutputFactoryConfigConverter implements TypeConfigConverter<OutputFactory> {

    public static final String TYPE = "output-factory";

    @Override
    public String type() {
        return TYPE;
    }

}
