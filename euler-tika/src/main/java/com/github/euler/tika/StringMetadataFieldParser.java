package com.github.euler.tika;

import org.apache.tika.metadata.Metadata;

public class StringMetadataFieldParser implements MetadataFieldParser {

    @Override
    public boolean accept(String name, Metadata metadata) {
        return true;
    }

    @Override
    public Object parse(String name, Metadata metadata) {
        return metadata.get(name);
    }

}
