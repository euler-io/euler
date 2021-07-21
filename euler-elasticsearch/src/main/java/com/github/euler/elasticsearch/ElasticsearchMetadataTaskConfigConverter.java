package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.elasticsearch.ElasticsearchMetadataTask.Builder;
import com.github.euler.elasticsearch.req.AbstractElasticSearchRequestFactoryConfigConverter;
import com.github.euler.elasticsearch.req.ElasticSearchRequestFactory;
import com.typesafe.config.Config;

public class ElasticsearchMetadataTaskConfigConverter extends AbstractElasticsearchTaskConfigConverter {

    @Override
    public String type() {
        return "elasticsearch-content-sink";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        String name = getName(config, tasksConfigConverter);

        RestHighLevelClient client = getClient(config, ctx, typesConfigConverter);
        Builder builder = ElasticsearchMetadataTask.builder(name, client);
        builder.setFlushConfig(getFlushConfig(config));
        builder.setIndex(getIndex(config));

        ElasticSearchRequestFactory<?> requestFactory = typesConfigConverter.convert(AbstractElasticSearchRequestFactoryConfigConverter.TYPE, config.getValue("request-type"), ctx);
        builder.setRequestFactory(requestFactory);

        return builder.build();
    }

}
