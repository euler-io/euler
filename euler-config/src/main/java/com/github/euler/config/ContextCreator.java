package com.github.euler.config;

import com.typesafe.config.ConfigValue;

public interface ContextCreator {

    String type();

    ConfigContext create(ConfigValue v, ConfigContext ctx);

}