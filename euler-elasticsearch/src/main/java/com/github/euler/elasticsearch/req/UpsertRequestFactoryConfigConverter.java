package com.github.euler.elasticsearch.req;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class UpsertRequestFactoryConfigConverter extends AbstractElasticSearchRequestFactoryConfigConverter {

    @Override
    public String configType() {
        return "upsert";
    }

    @Override
    public ElasticSearchRequestFactory<?> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new UpsertRequestFactory();
    }

}
