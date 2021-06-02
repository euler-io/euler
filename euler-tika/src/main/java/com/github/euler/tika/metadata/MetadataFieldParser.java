package com.github.euler.tika.metadata;

import org.apache.tika.metadata.Metadata;

public interface MetadataFieldParser {

    boolean accept(String name, Metadata metadata);

    Object parse(String name, Metadata metadata);

}
