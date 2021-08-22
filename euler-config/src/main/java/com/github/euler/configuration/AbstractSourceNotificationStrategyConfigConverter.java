package com.github.euler.configuration;

import com.github.euler.core.source.SourceNotificationStrategy;

public abstract class AbstractSourceNotificationStrategyConfigConverter implements TypeConfigConverter<SourceNotificationStrategy> {

    public static final String TYPE = "source-notification-strategy";

    @Override
    public String type() {
        return TYPE;
    }

}
