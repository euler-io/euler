package com.github.euler.elasticsearch.req;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractElasticSearchRequestFactoryConfigConverter implements TypeConfigConverter<ElasticSearchRequestFactory<?>> {

    public static final String TYPE = "request-type";

    @Override
    public String type() {
        return TYPE;
    }

}
