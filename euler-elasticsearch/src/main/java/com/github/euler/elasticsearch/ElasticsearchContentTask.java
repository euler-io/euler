package com.github.euler.elasticsearch;

import java.util.Objects;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.AbstractBatchTask;
import com.github.euler.common.CommonContext;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.FragmentBatch;

public class ElasticsearchContentTask extends AbstractBatchTask {

    private ElasticsearchContentTask(String name, String index, Parser parser, StreamFactory sf, int fragmentSize, int fragmentOverlap, RestHighLevelClient client,
            FlushConfig flushConfig) {
        super(name, () -> new FragmentBatch(parser, sf, fragmentSize, fragmentOverlap, new ElasticsearchFragmentSink(client, index, flushConfig)));
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return msg.ctx.context(CommonContext.INDEXABLE, true);
    }

    public static Builder builder(String name, StreamFactory streamFactory, RestHighLevelClient client) {
        return new Builder(name, streamFactory, client);
    }

    public static class Builder {

        private String name;
        private String index = null;
        private Parser parser = new AutoDetectParser();
        private StreamFactory streamFactory;
        private int fragmentSize = 1000;
        private int fragmentOverlap = 50;
        private RestHighLevelClient client;
        private FlushConfig flushConfig = new FlushConfig();

        private Builder(String name, StreamFactory streamFactory, RestHighLevelClient client) {
            super();
            this.name = name;
            this.streamFactory = streamFactory;
            this.client = client;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public String getIndex() {
            return index;
        }

        public Builder setIndex(String index) {
            this.index = index;
            return this;
        }

        public Parser getParser() {
            return parser;
        }

        public Builder setParser(Parser parser) {
            this.parser = parser;
            return this;
        }

        public StreamFactory getStreamFactory() {
            return streamFactory;
        }

        public Builder setStreamFactory(StreamFactory sf) {
            this.streamFactory = sf;
            return this;
        }

        public int getFragmentSize() {
            return fragmentSize;
        }

        public Builder setFragmentSize(int fragmentSize) {
            this.fragmentSize = fragmentSize;
            return this;
        }

        public int getFragmentOverlap() {
            return fragmentOverlap;
        }

        public Builder setFragmentOverlap(int fragmentOverlap) {
            this.fragmentOverlap = fragmentOverlap;
            return this;
        }

        public RestHighLevelClient getClient() {
            return client;
        }

        public Builder setClient(RestHighLevelClient client) {
            this.client = client;
            return this;
        }

        public FlushConfig getFlushConfig() {
            return flushConfig;
        }

        public Builder setFlushConfig(FlushConfig flushConfig) {
            this.flushConfig = flushConfig;
            return this;
        }

        public ElasticsearchContentTask build() {
            Objects.requireNonNull(name, () -> "name is required");
            Objects.requireNonNull(parser, () -> "parser is required");
            Objects.requireNonNull(streamFactory, () -> "streamFactory is required");
            Objects.requireNonNull(fragmentSize, () -> "fragmentSize is required");
            Objects.requireNonNull(fragmentOverlap, () -> "fragmentOverlap is required");
            Objects.requireNonNull(client, () -> "client is required");
            Objects.requireNonNull(flushConfig, () -> "flushConfig is required");
            return new ElasticsearchContentTask(name, index, parser, streamFactory, fragmentSize, fragmentOverlap, client, flushConfig);
        }

    }

}
