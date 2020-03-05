package com.github.euler.tika;

import org.apache.tika.metadata.Metadata;

public interface MetadataFieldParser {

    boolean accept(String name, Metadata metadata);

    Object parse(String name, Metadata metadata);

}
