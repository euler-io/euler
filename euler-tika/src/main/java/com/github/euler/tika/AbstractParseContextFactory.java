package com.github.euler.tika;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractParseContextFactory implements TypeConfigConverter<ParseContextFactory> {

    public static final String TYPE = "parse-context";

    public AbstractParseContextFactory() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}