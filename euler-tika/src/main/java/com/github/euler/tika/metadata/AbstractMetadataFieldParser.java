package com.github.euler.tika.metadata;

import org.apache.tika.metadata.Metadata;

public abstract class AbstractMetadataFieldParser implements MetadataFieldParser {

    @Override
    public Object parse(String name, Metadata metadata) {
        String[] values = metadata.getValues(name);
        return parse(name, metadata, values);
    }

    protected abstract Object parse(String name, Metadata metadata, String[] values);

}
