package com.github.euler.tika.embedded;

import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.EmbeddedNamingStrategy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class MimetypeEmbeddedNamingStrategyConfigConverter extends AbstractEmbeddedNamingStrategyConfigConverter {

    @Override
    public String configType() {
        return "mime-type";
    }

    @Override
    public EmbeddedNamingStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        EmbeddedNamingStrategy defaultStrategy = typeConfigConverter.convert(AbstractEmbeddedNamingStrategyConfigConverter.TYPE, config.getValue("default"), configContext);
        Map<String, EmbeddedNamingStrategy> mapping = config.getObject("mapping").entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> convert(e.getValue(), configContext, typeConfigConverter)));
        return new MimetypeEmbeddedNamingStrategy(defaultStrategy, mapping);
    }

    private EmbeddedNamingStrategy convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(AbstractEmbeddedNamingStrategyConfigConverter.TYPE, value, configContext);
    }

    protected Config getDefaultConfig() {
        URL resource = MimetypeEmbeddedNamingStrategyConfigConverter.class.getClassLoader().getResource("mimetypestrategy.conf");
        return ConfigFactory.parseURL(resource);
    }

}
