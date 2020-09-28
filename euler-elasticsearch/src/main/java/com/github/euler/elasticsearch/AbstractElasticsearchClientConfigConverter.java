package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractElasticsearchClientConfigConverter implements TypeConfigConverter<RestHighLevelClient> {

    public static final String TYPE = "elasticsearch-client";

    @Override
    public String type() {
        return TYPE;
    }

}
