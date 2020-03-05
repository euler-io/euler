package com.github.euler.tika;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Builder;

public class DefaultMetadataParser implements MetadataParser {

    private List<MetadataFieldParser> fieldParsers = new ArrayList<>();

    public DefaultMetadataParser() {
        super();
        add(new StringMetadataFieldParser());
    }

    public DefaultMetadataParser(List<MetadataFieldParser> fieldParsers) {
        super();
        this.fieldParsers = new ArrayList<>(fieldParsers);
    }

    public DefaultMetadataParser add(MetadataFieldParser fieldParser) {
        this.fieldParsers.add(fieldParser);
        return this;
    }

    @Override
    public ProcessingContext parse(Metadata metadata) {
        Builder builder = ProcessingContext.builder();
        for (String name : metadata.names()) {
            parseField(metadata, builder, name);
        }
        return builder.build();
    }

    protected void parseField(Metadata metadata, Builder builder, String name) {
        Object value = parseValue(metadata, name);
        builder.metadata(name, value);
    }

    protected Object parseValue(Metadata metadata, String name) {
        MetadataFieldParser fieldParser = findFieldParser(name, metadata);
        if (fieldParser == null) {
            throw new IllegalStateException("Could not find a field parser for '" + name + "'.");
        }

        Object value = fieldParser.parse(name, metadata);
        return value;
    }

    protected MetadataFieldParser findFieldParser(String name, Metadata metadata) {
        MetadataFieldParser parser = null;
        Iterator<MetadataFieldParser> iterator = fieldParsers.iterator();
        while (parser == null || iterator.hasNext()) {
            MetadataFieldParser next = iterator.next();
            if (next.accept(name, metadata)) {
                parser = next;
            }
        }
        return parser;
    }

}
