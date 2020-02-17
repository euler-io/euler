package com.github.euler.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessingContext {

    private final Map<String, Object> metadata;
    private final Map<String, Object> context;
    private final Action action;

    private ProcessingContext(Map<String, Object> metadata, Map<String, Object> context, Action action) {
        this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
        this.context = Collections.unmodifiableMap(new HashMap<>(context));
        this.action = action;
    }

    public Object metadata(String key) {
        return this.metadata.get(key);
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public Object context(String key) {
        return this.context.get(key);
    }

    public Map<String, Object> context() {
        return context;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProcessingContext merge(ProcessingContext o) {
        Map<String, Object> newMetadata = merge(this.metadata, o.metadata, this.action);
        Map<String, Object> newContext = new HashMap<>(this.context);
        return new ProcessingContext(newMetadata, newContext, this.action);
    }

    private Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2, Action action) {
        Map<String, Object> merged = new HashMap<>(m1);

        switch (action) {
            case OVERWRITE :
                merged.putAll(m2);
                break;
            case MERGE :
                m2.forEach((k, v) -> putIfAbsentOrMerge(merged, k, v));
                break;
            default :
                m2.forEach(merged::putIfAbsent);
                break;
        }

        return merged;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void putIfAbsentOrMerge(Map<String, Object> payload, String key, Object value) {
        Object current = payload.get(key);
        if (current == null) {
            payload.put(key, value);
        } else if (current instanceof List && value instanceof List) {
            List newList = new ArrayList((List) current);
            newList.addAll((List) value);
            payload.put(key, newList);
        } else if (current instanceof Set && value instanceof Set) {
            Set newSet = new HashSet((Set) current);
            newSet.addAll((Set) value);
            payload.put(key, newSet);
        }
    }

    public static enum Action {
        PUT_IF_ABSENT, OVERWRITE, MERGE
    }

    public static class Builder {

        private Map<String, Object> metadata;
        private Map<String, Object> context;
        private Action action = Action.PUT_IF_ABSENT;

        public Builder() {
            super();
            this.metadata = new HashMap<>();
            this.context = new HashMap<>();
        }

        public void metadata(String key, Object value) {
            this.metadata.put(key, value);
        }

        public ProcessingContext build() {
            return new ProcessingContext(this.metadata, this.context, this.action);
        }

        public void context(String key, Object value) {
            this.context.put(key, value);
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

    }

}
