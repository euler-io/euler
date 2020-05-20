package com.github.euler.common;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractStorageStrategyConfigConverter implements TypeConfigConverter<StorageStrategy> {

    private static final String STORAGE_STRATEGY = "storage-strategy";

    @Override
    public String type() {
        return STORAGE_STRATEGY;
    }

}
