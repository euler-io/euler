package com.github.euler.elasticsearch;

import java.net.URL;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.FlushConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public abstract class AbstractElasticsearchTaskConfigConverter extends AbstractTaskConfigConverter {

    protected FlushConfig getFlushConfig(Config config) {
        int minActionsToFlush = config.getInt("flush.min-actions");
        int maxActionsToFlush = config.getInt("flush.max-actions");
        long minBytesToFlush = config.getInt("flush.min-bytes");
        long maxBytesToFlush = config.getInt("flush.max-bytes");
        return new FlushConfig(minActionsToFlush, maxActionsToFlush, minBytesToFlush, maxBytesToFlush);
    }

    protected RestHighLevelClient getClient(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, config.getValue("elasticsearch-client"), ctx);
    }

    protected String getIndex(Config config) {
        if (config.hasPath("index")) {
            return config.getString("index");
        } else {
            return null;
        }
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = ElasticsearchSourceConfigConverter.class.getClassLoader().getResource("elasticsearchtask.conf");
        return ConfigFactory.parseURL(resource);
    }
}
