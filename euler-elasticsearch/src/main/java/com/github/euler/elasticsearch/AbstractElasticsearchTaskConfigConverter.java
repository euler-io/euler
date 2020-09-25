package com.github.euler.elasticsearch;

import java.net.URL;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.tika.FlushConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public abstract class AbstractElasticsearchTaskConfigConverter extends AbstractTaskConfigConverter {

    protected FlushConfig getFlushConfig(Config config) {
        int minActionsToFlush = config.getInt("flush.min-actions");
        int maxActionsToFlush = config.getInt("flush.max-actions");
        long minBytesToFlush = config.getInt("flush.min-bytes");
        long maxBytesToFlush = config.getInt("flush.max-bytes");
        return new FlushConfig(minActionsToFlush, maxActionsToFlush, minBytesToFlush, maxBytesToFlush);
    }

    protected RestHighLevelClient getClient(Config config, ConfigContext ctx) {
        RestHighLevelClient client = ctx.get(RestHighLevelClient.class);
        if (client == null) {
            client = ElasticsearchUtils.initializeClient(config.getConfig("elasticsearch"));
        }
        return client;
    }

    protected String getIndex(Config config) {
        if (config.hasPath("index")) {
            return config.getString("index");
        } else {
            return null;
        }
    }

    protected Config getDefaultConfig() {
        URL resource = ElasticsearchSourceConfigConverter.class.getClassLoader().getResource("/elasticsearchtask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
