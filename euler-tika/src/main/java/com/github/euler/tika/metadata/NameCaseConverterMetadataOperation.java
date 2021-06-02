package com.github.euler.tika.metadata;

public class NameCaseConverterMetadataOperation implements MetadataOperation {

    private final Case nameCase;

    public NameCaseConverterMetadataOperation(Case nameCase) {
        super();
        this.nameCase = nameCase;
    }

    @Override
    public String runOnName(String name) {
        switch (nameCase) {
        case UPPER:
            return name.toUpperCase();
        default:
            return name.toLowerCase();
        }
    }

    public static enum Case {
        LOWER, UPPER
    }

}
