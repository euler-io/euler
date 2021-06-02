package com.github.euler.tika.metadata;

import org.apache.tika.metadata.Metadata;

public class StringMetadataFieldParser extends AbstractMetadataFieldParser {

    @Override
    public boolean accept(String name, Metadata metadata) {
        return true;
    }

    @Override
    protected Object parse(String name, Metadata metadata, String[] values) {
        if (values.length == 1) {
            return values[0];
        } else {
            return values;
        }
    }

}
