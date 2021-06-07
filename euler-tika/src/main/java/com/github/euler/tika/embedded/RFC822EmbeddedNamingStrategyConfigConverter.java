package com.github.euler.tika.embedded;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.EmbeddedNamingStrategy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RFC822EmbeddedNamingStrategyConfigConverter extends AbstractEmbeddedNamingStrategyConfigConverter {

    @Override
    public String configType() {
        return "rfc822";
    }

    @Override
    public EmbeddedNamingStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String identifierRegex = config.getString("identifier-regex");
        return new RFC822EmbeddedNamingStrategy(identifierRegex);
    }

    protected Config getDefaultConfig() {
        URL resource = RFC822EmbeddedNamingStrategyConfigConverter.class.getClassLoader().getResource("rfc822namingstrategy.conf");
        return ConfigFactory.parseURL(resource);
    }

}
