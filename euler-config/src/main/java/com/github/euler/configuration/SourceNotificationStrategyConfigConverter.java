package com.github.euler.configuration;

import com.github.euler.core.source.SourceNotificationStrategy;
import com.typesafe.config.ConfigValue;

public class SourceNotificationStrategyConfigConverter implements ContextConfigConverter {

    private static final String SOURCE_NOTIFICATION_STRATEGY = "source-notification-strategy";

    @Override
    public String path() {
        return SOURCE_NOTIFICATION_STRATEGY;
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typesConfigConverter) {
        SourceNotificationStrategy sourceNotificationStrategy = typesConfigConverter.convert(AbstractSourceNotificationStrategyConfigConverter.TYPE, value, configContext);
        return ConfigContext.builder()
                .put(SourceNotificationStrategy.class, sourceNotificationStrategy)
                .build();
    }

}
