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
        config = getConfig(config);
        RestHighLevelClient client = getClient(config, ctx, typeConfigConverter);
        String query = config.getConfig("query").root().render(ConfigRenderOptions.concise());
        int size = config.getInt("size");
        String scroll = config.getString("scroll-keep-alive");
        String[] sourceIncludes = config.getStringList("_source.includes").stream().toArray(s -> new String[s]);
        String[] sourceExcludes = config.getStringList("_source.excludes").stream().toArray(s -> new String[s]);
        return PausableSourceExecution.create(new ElasticsearchSource(client, query, size, scroll, sourceIncludes, sourceExcludes));
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected RestHighLevelClient getClient(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, config.getValue("elasticsearch-client"), ctx);
    }

    protected Config getDefaultConfig() {
        URL resource = ElasticsearchSourceConfigConverter.class.getClassLoader().getResource("elasticsearchsource.conf");
        return ConfigFactory.parseURL(resource);
    }

}
