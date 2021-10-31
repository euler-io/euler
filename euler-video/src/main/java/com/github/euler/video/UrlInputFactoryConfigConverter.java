package com.github.euler.video;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class UrlInputFactoryConfigConverter extends AbstractInputFactoryConfigConverter {

    @Override
    public String configType() {
        return "url";
    }

    @Override
    public InputFactory convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new URLInputOutputFactory();
    }

}
