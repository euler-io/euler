package com.github.euler.common;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractStreamFactoryConfigConverter implements TypeConfigConverter<StreamFactory> {

    @Override
    public String type() {
        return StreamFactoryContextConfigConverter.STREAM_FACTORY;
    }

}
