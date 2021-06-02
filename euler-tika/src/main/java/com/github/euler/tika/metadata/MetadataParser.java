package com.github.euler.tika.metadata;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;

public interface MetadataParser {

    ProcessingContext parse(Metadata metadata);

}
