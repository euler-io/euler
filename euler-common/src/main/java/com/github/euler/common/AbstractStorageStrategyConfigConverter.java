package com.github.euler.common;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractStorageStrategyConfigConverter implements TypeConfigConverter<StorageStrategy> {

    public static final String TYPE = "storage-strategy";

    @Override
    public String type() {
        return TYPE;
    }

}
