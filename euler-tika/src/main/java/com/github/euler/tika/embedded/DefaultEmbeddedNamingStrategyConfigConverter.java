package com.github.euler.tika.embedded;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.EmbeddedNamingStrategy;
import com.typesafe.config.Config;

public class DefaultEmbeddedNamingStrategyConfigConverter extends AbstractEmbeddedNamingStrategyConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public EmbeddedNamingStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new DefaultEmbeddedNamingStrategy();
    }

}
