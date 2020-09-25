package com.github.euler.elasticsearch;

import java.net.URL;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.AbstractSourceConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.PausableSourceExecution;
import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

import akka.actor.typed.Behavior;

public class ElasticsearchSourceConfigConverter extends AbstractSourceConfigConverter {

    @Override
    public String configType() {
        return "elasticsearch";
    }

    @Override
    public Behavior<SourceCommand> convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        RestHighLevelClient client = getClient(config, ctx);
        String query = config.getConfig("query").root().render(ConfigRenderOptions.concise());
        int size = config.getInt("size");
        String scroll = config.getString("scroll-keep-alive");
        return PausableSourceExecution.create(new ElasticsearchSource(client, query, size, scroll));
    }

    protected RestHighLevelClient getClient(Config config, ConfigContext ctx) {
        RestHighLevelClient client = ctx.get(RestHighLevelClient.class);
        if (client == null) {
            client = ElasticsearchUtils.initializeClient(config.getConfig("elasticsearch"));
        }
        return client;
    }

    protected Config getDefaultConfig() {
        URL resource = ElasticsearchSourceConfigConverter.class.getClassLoader().getResource("/elasticsearchsource.conf");
        return ConfigFactory.parseURL(resource);
    }

}
