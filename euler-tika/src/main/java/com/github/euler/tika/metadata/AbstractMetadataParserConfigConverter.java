package com.github.euler.tika.metadata;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMetadataParserConfigConverter implements TypeConfigConverter<MetadataParser> {

    public static final String TYPE = "metadata-parser";

    public AbstractMetadataParserConfigConverter() {
        super();
    }

    @Override
    public String type() {
        return TYPE;
    }

}