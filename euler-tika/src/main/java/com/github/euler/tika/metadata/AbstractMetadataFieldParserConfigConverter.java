package com.github.euler.tika.metadata;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMetadataFieldParserConfigConverter implements TypeConfigConverter<MetadataFieldParser> {

    public static final String TYPE = "metadata-field-parser";

    public AbstractMetadataFieldParserConfigConverter() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}