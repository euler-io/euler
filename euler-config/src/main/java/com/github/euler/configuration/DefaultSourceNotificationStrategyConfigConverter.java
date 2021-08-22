package com.github.euler.configuration;

import java.net.URL;
import java.time.Duration;

import com.github.euler.core.source.DefaultSourceNotificationStrategy;
import com.github.euler.core.source.SourceNotificationStrategy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class DefaultSourceNotificationStrategyConfigConverter extends AbstractSourceNotificationStrategyConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public SourceNotificationStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        int minProcessedItems = config.getInt("min-processed-items");
        int maxProcessedItems = config.getInt("max-processed-items");
        Duration minInterval = config.getDuration("min-interval-notification");
        Duration maxInterval = getMaxInterval(config);
        int maxNumberIgnoredRequests = config.getInt("max-number-ignored-requests");
        return new DefaultSourceNotificationStrategy(minProcessedItems, maxProcessedItems, minInterval, maxInterval, maxNumberIgnoredRequests);
    }

    protected Duration getMaxInterval(Config config) {
        return config.getDuration("max-interval-notification");
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("defaultsourcenotificationstrategy.conf");
        return ConfigFactory.parseURL(resource);
    }

}
