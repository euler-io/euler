package com.github.euler.opencv;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractDnnNetLoaderConfigConverter implements TypeConfigConverter<DnnNetLoader> {

    public static final String TYPE = "dnn-net-loader";

    @Override
    public String type() {
        return TYPE;
    }

}
