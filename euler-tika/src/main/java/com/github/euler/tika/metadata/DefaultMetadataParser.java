package com.github.euler.tika.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Builder;

public class DefaultMetadataParser implements MetadataParser {

    private final Pattern includeField;
    private final Pattern excludeField;
    private final List<MetadataFieldParser> fieldParsers;

    public DefaultMetadataParser() {
        this(new ArrayList<>());
        add(new StringMetadataFieldParser());
    }

    public DefaultMetadataParser(List<MetadataFieldParser> fieldParsers) {
        this(".+", "a^", fieldParsers);
    }

    public DefaultMetadataParser(String includeField, String excludeField, List<MetadataFieldParser> fieldParsers) {
        super();
        this.includeField = Pattern.compile(includeField);
        this.excludeField = Pattern.compile(excludeField);
        this.fieldParsers = fieldParsers;
    }

    public DefaultMetadataParser add(MetadataFieldParser fieldParser) {
        this.fieldParsers.add(fieldParser);
        return this;
    }

    @Override
    public ProcessingContext parse(Metadata metadata) {
        Builder builder = ProcessingContext.builder();
        for (String name : metadata.names()) {
            if (includeField(name)) {
                parseField(metadata, builder, name);
            }
        }
        return builder.build();
    }

    private boolean includeField(String name) {
        boolean included = this.includeField.matcher(name).matches();
        boolean excluded = this.excludeField.matcher(name).matches();

        return included && !excluded;
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
