package com.github.euler.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessingContext {

    public static final ProcessingContext EMPTY = new ProcessingContext(Collections.emptyMap(), Collections.emptyMap(), Action.PUT_IF_ABSENT);

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

    @SuppressWarnings("unchecked")
    public <T> T context(String key, T defaultValue) {
        if (context.containsKey(key)) {
            T value = (T) context(key);
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessingContext other = (ProcessingContext) obj;
        if (action != other.action)
            return false;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
            return false;
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProcessingContext merge(ProcessingContext o) {
        Map<String, Object> newMetadata = merge(this.metadata, o.metadata, o.action);
        Map<String, Object> newContext = merge(this.context, o.context, o.action);
        return new ProcessingContext(newMetadata, newContext, this.action);
    }

    public ProcessingContext merge(ProcessingContext p1, ProcessingContext p2) {
        return p1.merge(p2);
    }

    private Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2, Action action) {
        Map<String, Object> merged = new HashMap<>(m1);

        switch (action) {
        case OVERWRITE:
            merged.putAll(m2);
            break;
        case MERGE:
            m2.forEach((k, v) -> putIfAbsentOrMerge(merged, k, v));
            break;
        default:
            m2.forEach(merged::putIfAbsent);
            break;
        }

        return merged;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void putIfAbsentOrMerge(Map<String, Object> payload, String key, Object value) {
        Object current = payload.get(key);
        if (current == null) {
            payload.put(key, value);
        } else if (current instanceof List && value instanceof List) {
            List newList = new ArrayList((List) current);
            newList.addAll((List) value);
            payload.put(key, Collections.unmodifiableList(newList));
        } else if (current instanceof Set && value instanceof Set) {
            Set newSet = new HashSet((Set) current);
            newSet.addAll((Set) value);
            payload.put(key, Collections.unmodifiableSet(newSet));
        }
    }

    public static enum Action {
        PUT_IF_ABSENT, OVERWRITE, MERGE
    }

    public static class Builder {

        private Map<String, Object> metadata;
        private Map<String, Object> context;
        private Action action = Action.OVERWRITE;

        public Builder() {
            super();
            this.metadata = new HashMap<>();
            this.context = new HashMap<>();
        }

        public ProcessingContext build() {
            return new ProcessingContext(this.metadata, this.context, this.action);
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder context(String key, Object value) {
            this.context.put(key, value);
            return this;
        }

        public Action getAction() {
            return action;
        }

        public Builder setAction(Action action) {
            this.action = action;
            return this;
        }

    }

}
