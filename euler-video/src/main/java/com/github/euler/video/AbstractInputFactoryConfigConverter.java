package com.github.euler.video;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractInputFactoryConfigConverter implements TypeConfigConverter<InputFactory> {

    public static final String TYPE = "input-factory";

    @Override
    public String type() {
        return TYPE;
    }

}
