package com.github.euler.elasticsearch;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.github.euler.elasticsearch.ElasticSearchTask.Builder;
import com.github.euler.tika.FlushConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ElasticSearchTaskConfigConverter extends AbstractTaskConfigConverter {

    private static final String FRAGMENT_SIZE = "fragment-size";
    private static final String FRAGMENT_OVERLAP = "fragment-overlap";

    @Override
    public String type() {
        return "elasticsearch-sink";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);

        RestHighLevelClient client = getClient(config, ctx);
        Builder builder = ElasticSearchTask.builder(name, streamFactory, client);
        builder.setParser(ctx.get(Parser.class, new AutoDetectParser()));
        builder.setFragmentSize(config.getInt(FRAGMENT_SIZE));
        builder.setFragmentOverlap(config.getInt(FRAGMENT_OVERLAP));
        builder.setFlushConfig(getFlushConfig(config));

        return builder.build();
    }

    private FlushConfig getFlushConfig(Config config) {
        int minActionsToFlush = config.getInt("flush.min-actions");
        int maxActionsToFlush = config.getInt("flush.max-actions");
        long minBytesToFlush = config.getInt("flush.min-bytes");
        long maxBytesToFlush = config.getInt("flush.max-bytes");
        return new FlushConfig(minActionsToFlush, maxActionsToFlush, minBytesToFlush, maxBytesToFlush);
    }

    private RestHighLevelClient getClient(Config config, ConfigContext ctx) {
        RestHighLevelClient client = ctx.get(RestHighLevelClient.class);
        if (client == null) {
            client = ElasticSearchUtils.initializeClient(config.getConfig("elasticsearch"));
        }
        return client;
    }

    public Config getDefaultConfig() {
        return ConfigFactory.load("elasticsearhtask.conf");
    }

}
