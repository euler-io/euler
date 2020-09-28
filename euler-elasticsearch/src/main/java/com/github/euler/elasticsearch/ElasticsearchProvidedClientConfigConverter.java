package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class ElasticsearchProvidedClientConfigConverter extends AbstractElasticsearchClientConfigConverter {

    @Override
    public String configType() {
        return "provided";
    }

    @Override
    public RestHighLevelClient convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return configContext.getRequired(RestHighLevelClient.class);
    }

}
