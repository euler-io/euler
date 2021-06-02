package com.github.euler.tika.metadata;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ValueReplaceMetadataOperation implements MetadataOperation {

    private final Pattern patternName;
    private final Pattern pattern;
    private final String replacement;

    public ValueReplaceMetadataOperation(String nameRegex, String regex, String replacement) {
        super();
        this.patternName = Pattern.compile(nameRegex);
        this.pattern = Pattern.compile(regex);
        this.replacement = replacement;
    }

    @Override
    public Object runOnValue(String name, Object value) {
        if (patternName.matcher(name).matches()) {
            if (value instanceof String) {
                return pattern.matcher(value.toString()).replaceAll(replacement);
            } else if (value instanceof String[]) {
                String[] arr = (String[]) value;
                return Arrays.stream(arr)
                        .map(v -> pattern.matcher(v).replaceAll(replacement))
                        .toArray(s -> new String[s]);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

}
