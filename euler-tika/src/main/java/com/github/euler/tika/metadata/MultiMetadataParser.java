package com.github.euler.tika.metadata;

import java.util.List;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;

public class MultiMetadataParser implements MetadataParser {

    private final List<MetadataParser> parsers;

    public MultiMetadataParser(List<MetadataParser> parsers) {
        super();
        this.parsers = parsers;
    }

    @Override
    public ProcessingContext parse(Metadata metadata) {
        return parsers.stream()
                .map(p -> p.parse(metadata))
                .reduce(ProcessingContext.EMPTY, (c1, c2) -> c1.merge(c2));
    }

}
