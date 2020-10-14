package com.github.euler.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class TypesConfigConverter {

    public static final String TYPE = "type";

    private final Map<String, Map<String, TypeConfigConverter<?>>> typeConverterMap;

    TypesConfigConverter(List<TypeConfigConverter<?>> converters) {
        super();
        this.typeConverterMap = new HashMap<>();
        for (TypeConfigConverter<?> converter : converters) {
            String type = converter.type();
            typeConverterMap.computeIfAbsent(type, (k) -> new HashMap<>());
            typeConverterMap.get(type).put(converter.configType(), converter);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(String type, ConfigValue value, ConfigContext configContext) {
        Config config;
        String configType;
        switch (value.valueType()) {
        case STRING:
            config = ConfigFactory.empty().withValue(TYPE, value);
            configType = value.unwrapped().toString();
            break;
        case OBJECT:
            config = ((ConfigObject) value).toConfig();
            configType = config.getString(TYPE);
            break;
        default:
            config = null;
            configType = null;
            break;
        }
        Map<String, TypeConfigConverter<?>> configMap = typeConverterMap.get(type);
        Objects.requireNonNull(configMap, () -> "Could not find any configuration for type: " + type + ".");
        TypeConfigConverter<?> typeConfigConverter = configMap.get(configType);
        Objects.requireNonNull(typeConfigConverter, () -> "Could not find any configuration of " + configType + " for type: " + type + ".");
        return (T) typeConfigConverter.convert(config, configContext, this);
    }

}
