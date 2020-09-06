package com.github.euler.elasticsearch;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.elasticsearch.ElasticsearchContentTask.Builder;
import com.typesafe.config.Config;

public class ElasticsearchFragmentTaskConfigConverter extends AbstractElasticsearchTaskConfigConverter {

    private static final String FRAGMENT_SIZE = "fragment-size";
    private static final String FRAGMENT_OVERLAP = "fragment-overlap";

    @Override
    public String type() {
        return "elasticsearch-fragment-sink";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);

        RestHighLevelClient client = getClient(config, ctx);
        Builder builder = ElasticsearchContentTask.builder(name, streamFactory, client);
        builder.setParser(ctx.get(Parser.class, new AutoDetectParser()));
        builder.setFragmentSize(config.getInt(FRAGMENT_SIZE));
        builder.setFragmentOverlap(config.getInt(FRAGMENT_OVERLAP));
        builder.setFlushConfig(getFlushConfig(config));
        builder.setIndex(getIndex(config));

        return builder.build();
    }

}
