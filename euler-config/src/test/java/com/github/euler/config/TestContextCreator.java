package com.github.euler.config;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class TestContextCreator implements ContextCreator {

    @Override
    public String type() {
        return "test";
    }

    @Override
    public ConfigContext create(ConfigValue v, ConfigContext ctx) {
        ConfigContext.Builder builder = ConfigContext.builder();
        ConfigObject configObject = (ConfigObject) v;
        configObject.forEach((key, value) -> builder.put(key, value.unwrapped()));
        return builder.build();
    }

}
