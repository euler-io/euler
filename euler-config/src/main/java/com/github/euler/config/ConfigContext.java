package com.github.euler.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigContext {

    public static final ConfigContext EMPTY = new ConfigContext(Collections.emptyMap());

    private final Map<String, Object> ctx;

    private ConfigContext(Map<String, Object> ctx) {
        this.ctx = Collections.unmodifiableMap(new HashMap<>(ctx));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Object get(String key) {
        return ctx.get(key);
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

        public void put(String key, Object value) {
            ctx.put(key, value);
        }

        public ConfigContext build() {
            return new ConfigContext(this.ctx);
        }

    }

}
