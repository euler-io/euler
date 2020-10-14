package com.github.euler.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.AbstractBatchBarrierConditionConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.BatchBarrierCondition;
import com.typesafe.config.Config;

public class ExistsInIndexConditionConfigConverter extends AbstractBatchBarrierConditionConfigConverter {

    @Override
    public String configType() {
        return "exists-in-index";
    }

    @Override
    public BatchBarrierCondition convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        String index = config.getString("index");
        RestHighLevelClient client = getClient(config, ctx, typeConfigConverter);
        return new ExistsInIndexCondition(index, client);
    }

    private RestHighLevelClient getClient(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter) {
        return typeConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, config.getValue("elasticsearch-client"), ctx);
    }

}
