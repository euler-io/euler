package com.github.euler.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProcessingContext {

    private Map<String, Object> metadata;

    public ProcessingContext(Map<String, Object> metadata) {
        this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public Object metadata(String key) {
        return this.metadata.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, Object> metadata;

        public Builder() {
            super();
            this.metadata = new HashMap<>();
        }

        public void metadata(String key, String value) {
            this.metadata.put(key, value);
        }

        public ProcessingContext build() {
            return new ProcessingContext(this.metadata);
        }

    }

}
