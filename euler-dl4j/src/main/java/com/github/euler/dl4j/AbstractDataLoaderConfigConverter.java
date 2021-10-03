package com.github.euler.dl4j;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractDataLoaderConfigConverter implements TypeConfigConverter<DataLoader> {

    public static final String TYPE = "data-loader";

    @Override
    public String type() {
        return TYPE;
    }

}
