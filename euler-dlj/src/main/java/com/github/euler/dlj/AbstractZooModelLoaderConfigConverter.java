package com.github.euler.dlj;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractZooModelLoaderConfigConverter<I, O> implements TypeConfigConverter<ZooModelLoader<I, O>> {

    public static final String TYPE = "zoo-model-loader";

    @Override
    public String type() {
        return TYPE;
    }

}
