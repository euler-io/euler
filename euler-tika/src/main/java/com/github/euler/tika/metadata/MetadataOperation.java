package com.github.euler.tika.metadata;

public interface MetadataOperation {

    default String runOnName(String name) {
        return name;
    }

    default Object runOnValue(String name, Object value) {
        return value;
    }

}
