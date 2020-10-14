package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.elasticsearch.ElasticsearchMetadataTask.Builder;
import com.typesafe.config.Config;

public class ElasticsearchMetadataTaskConfigConverter extends AbstractElasticsearchTaskConfigConverter {

    @Override
    public String type() {
        return "elasticsearch-content-sink";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);

        RestHighLevelClient client = getClient(config, ctx, typeConfigConverter);
        Builder builder = ElasticsearchMetadataTask.builder(name, client);
        builder.setFlushConfig(getFlushConfig(config));
        builder.setIndex(getIndex(config));

        return builder.build();
    }

}
