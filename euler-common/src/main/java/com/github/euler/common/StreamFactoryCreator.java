package com.github.euler.common;

import com.github.euler.config.ConfigContext;
import com.typesafe.config.Config;

public interface StreamFactoryCreator {

    String type();

    StreamFactory create(Config config, ConfigContext ctx);

}
