package com.github.euler.common;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class URIHashIdCalculatorConfigCalculator extends AbstractIdCalculatorConfigConverter {

    @Override
    public String configType() {
        return "hash-uri";
    }

    @Override
    public IdCalculator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new URIHashIdCalculator();
    }

}
