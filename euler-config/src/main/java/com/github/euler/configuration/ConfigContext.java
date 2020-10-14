package com.github.euler.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigContext {

    public static final ConfigContext EMPTY = new ConfigContext(Collections.emptyMap());

    private final Map<String, Object> ctx;

    private ConfigContext(Map<String, Object> ctx) {
        this.ctx = Collections.unmodifiableMap(new HashMap<>(ctx));
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean containsKey(String key) {
        return ctx.containsKey(key);
    }

    public boolean contains(Class<?> clazz) {
        return ctx.containsKey(clazz.getName());
    }

    public Object get(String key) {
        return ctx.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) get(clazz.getName());
    }

    public <T> T get(Class<T> clazz, T defaultValue) {
        T value = get(clazz);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public <T> T getRequired(Class<T> clazz) {
        T obj = get(clazz);
        Objects.requireNonNull(obj, () -> clazz.getName() + " is required.");
        return obj;
    }

    public Object getRequired(String key) {
        Object obj = get(key);
        Objects.requireNonNull(obj, () -> key + " is required.");
        return obj;
    }

    public ConfigContext merge(ConfigContext o) {
        Map<String, Object> newCtx = new HashMap<>(o.ctx);
        newCtx.putAll(ctx);
        return new ConfigContext(newCtx);
    }

    public static class Builder {

        private Map<String, Object> ctx = new HashMap<>();

        private Builder() {
            super();
        }

        public Builder put(String key, Object value) {
            ctx.put(key, value);
            return this;
        }

        public <T> Builder put(Class<T> clazz, T value) {
            put(clazz.getName(), value);
            return this;
        }

        public ConfigContext build() {
            return new ConfigContext(this.ctx);
        }

        public Builder putAll(ConfigContext ctx) {
            this.ctx.putAll(ctx.ctx);
            return this;
        }

    }

}
