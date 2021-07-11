package com.github.euler.tika.metadata;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ValueRegexExtractMetadataOperation implements MetadataOperation {

    private final Pattern namePattern;
    private final Pattern pattern;

    public ValueRegexExtractMetadataOperation(String nameRegex, String regex) {
        super();
        this.namePattern = Pattern.compile(nameRegex);
        this.pattern = Pattern.compile(regex);
    }

    public ValueRegexExtractMetadataOperation(String regex) {
        this(".+", regex);
    }

    @Override
    public Object runOnValue(String name, Object value) {
        if (namePattern.matcher(name).matches()) {
            if (value instanceof String) {
                return extract(value.toString())
                        .toArray(s -> new String[s]);
            } else if (value instanceof String[]) {
                String[] arr = (String[]) value;
                return Arrays.stream(arr)
                        .map(v -> extract(v))
                        .flatMap(s -> s.stream())
                        .collect(Collectors.toSet())
                        .toArray(s -> new String[s]);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    public Set<String> extract(String value) {
        Set<String> found = new TreeSet<>();
        String fieldValue = value.toString();
        Matcher matcher = pattern.matcher(fieldValue);
        while (matcher.find()) {
            found.add(matcher.group(0));
        }
        return found;
    }

}
