package com.github.euler.elasticsearch;

import java.net.URL;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.ItemProcessorTask;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ElasticsearchDeleteParentChildTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "delete-parent-child";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        RestHighLevelClient client = getClient(config, ctx, typeConfigConverter);
        String childType = config.getString("child-type");
        String globalIndex = getIndex(config);
        ElasticsearchDeleteParentChildProcessor itemProcessor = new ElasticsearchDeleteParentChildProcessor(client, childType, globalIndex);
        return new ItemProcessorTask(getName(config, tasksConfigConverter), itemProcessor);
    }

    private RestHighLevelClient getClient(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, config.getValue("elasticsearch-client"), ctx);
    }

    private String getIndex(Config config) {
        if (config.hasPath("index")) {
            return config.getString("index");
        } else {
            return null;
        }
    }

    private Config getDefaultConfig() {
        URL resource = ElasticsearchSourceConfigConverter.class.getClassLoader().getResource("elasticsearchparentchildtask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
