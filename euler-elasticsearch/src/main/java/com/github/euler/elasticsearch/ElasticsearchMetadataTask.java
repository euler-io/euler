package com.github.euler.elasticsearch;

import java.util.Objects;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.AbstractBatchTask;
import com.github.euler.common.CommonContext;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.MetadataBatch;

public class ElasticsearchMetadataTask extends AbstractBatchTask {

    private ElasticsearchMetadataTask(String name, String index, RestHighLevelClient client, FlushConfig flushConfig) {
        super(name, () -> new MetadataBatch(new ElasticsearchMetadataSink(client, index, flushConfig)));
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return msg.ctx.context(CommonContext.INDEXABLE, true);
    }

    public static Builder builder(String name, RestHighLevelClient client) {
        return new Builder(name, client);
    }

    public static class Builder {

        private String name;
        private String index = null;
        private RestHighLevelClient client;
        private FlushConfig flushConfig = new FlushConfig();

        private Builder(String name, RestHighLevelClient client) {
            super();
            this.name = name;
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

        public ElasticsearchMetadataTask build() {
            Objects.requireNonNull(name, () -> "name is required");
            Objects.requireNonNull(client, () -> "client is required");
            Objects.requireNonNull(flushConfig, () -> "flushConfig is required");
            return new ElasticsearchMetadataTask(name, index, client, flushConfig);
        }
    }

}
