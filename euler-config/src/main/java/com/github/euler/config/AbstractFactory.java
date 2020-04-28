package com.github.euler.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

public abstract class AbstractFactory<T> {

    private static final String TYPE = "type";

    public T create(ConfigValue value) {
        if (value.valueType() == ConfigValueType.STRING) {
            String type = value.unwrapped().toString();
            return create(type, ConfigFactory.empty());
        } else if (value.valueType() == ConfigValueType.OBJECT) {
            ConfigObject obj = (ConfigObject) value;
            Config config = obj.toConfig();
            String type = config.getString(TYPE);
            return create(type, config);
        } else {
            return null;
        }
    }

    protected abstract T create(String type, Config config);

}
