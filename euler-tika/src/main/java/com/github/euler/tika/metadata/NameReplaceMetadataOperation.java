package com.github.euler.tika.metadata;

import java.util.regex.Pattern;

public class NameReplaceMetadataOperation implements MetadataOperation {

    private final Pattern pattern;
    private final String replacement;

    public NameReplaceMetadataOperation(String regex, String replacement) {
        super();
        this.pattern = Pattern.compile(regex);
        this.replacement = replacement;
    }

    @Override
    public String runOnName(String name) {
        return pattern.matcher(name).replaceAll(replacement);
    }

}
