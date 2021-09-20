package com.github.euler.elasticsearch;

import org.apache.tika.parser.txt.TXTParser;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.elasticsearch.ElasticsearchContentTask.Builder;
import com.github.euler.elasticsearch.req.AbstractElasticSearchRequestFactoryConfigConverter;
import com.github.euler.elasticsearch.req.ElasticSearchRequestFactory;
import com.typesafe.config.Config;

public class ElasticsearchFragmentTaskConfigConverter extends AbstractElasticsearchTaskConfigConverter {

    private static final String FRAGMENT_TYPE = "fragment_type";
    private static final String FRAGMENT_SIZE = "fragment-size";
    private static final String FRAGMENT_OVERLAP = "fragment-overlap";

    @Override
    public String type() {
        return "elasticsearch-fragment-sink";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        String name = getName(config, tasksConfigConverter);
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);

        RestHighLevelClient client = getClient(config, ctx, typeConfigConverter);
        Builder builder = ElasticsearchContentTask.builder(name, streamFactory, client);
        builder.setParser(new TXTParser());
        builder.setFragmentSize(config.getInt(FRAGMENT_SIZE));
        builder.setFragmentOverlap(config.getInt(FRAGMENT_OVERLAP));
        builder.setFlushConfig(getFlushConfig(config));
        builder.setIndex(getIndex(config));

        ElasticSearchRequestFactory<?> requestFactory = typeConfigConverter.convert(AbstractElasticSearchRequestFactoryConfigConverter.TYPE, config.getValue("request-type"), ctx);
        builder.setRequestFactory(requestFactory);

        if (config.hasPath(FRAGMENT_TYPE)) {
            builder.setFragmentType(config.getString(FRAGMENT_TYPE));
        }

        return builder.build();
    }

}
