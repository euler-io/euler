package com.github.euler.tika.metadata;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;

public class IgnoreMetadataParser implements MetadataParser {

    @Override
    public ProcessingContext parse(Metadata metadata) {
        return ProcessingContext.EMPTY;
    }

}
