package com.github.euler.tika.metadata;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMetadataOperationConfigConverter implements TypeConfigConverter<MetadataOperation> {

    public static final String TYPE = "metadata-operation";

    @Override
    public String type() {
        return TYPE;
    }

}