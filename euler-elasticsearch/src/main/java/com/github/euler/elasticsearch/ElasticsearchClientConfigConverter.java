package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.ConfigValue;

public class ElasticsearchClientConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "elasticsearch-client";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typesConfigConverter) {
        RestHighLevelClient client = typesConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, value, configContext);
        return ConfigContext.builder()
                .put(RestHighLevelClient.class, client)
                .build();
    }

}
