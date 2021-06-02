package com.github.euler.tika.metadata;

import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;

public class ObjectMetadataParser extends DefaultMetadataParser implements MetadataParser {

    private final String field;

    public ObjectMetadataParser(String field, String includeField, String excludeField, List<MetadataFieldParser> fieldParsers) {
        super(includeField, excludeField, fieldParsers);
        this.field = field;
    }

    @Override
    public ProcessingContext parse(Metadata metadata) {
        ProcessingContext ctx = super.parse(metadata);
        return ProcessingContext.builder()
                .metadata(field, Map.copyOf(ctx.metadata()))
                .build();
    }

}
