package com.github.euler.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.CommonContext;
import com.github.euler.common.SizeUtils;
import com.github.euler.core.ProcessingContext;
import com.github.euler.tika.EmptyResponse;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.MetadataBatchSink;
import com.github.euler.tika.SinkResponse;

public class ElasticsearchMetadataSink implements MetadataBatchSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchMetadataSink.class);

    private final RestHighLevelClient client;
    private final FlushConfig flushConfig;

    private BulkRequest bulkRequest;
    private String globalIndex;
    private String index;

    public ElasticsearchMetadataSink(RestHighLevelClient client, String index, FlushConfig flushConfig) {
        super();
        this.client = client;
        this.globalIndex = index;
        this.flushConfig = flushConfig;
        this.bulkRequest = new BulkRequest();
    }

    @Override
    public SinkResponse store(URI uri, ProcessingContext ctx) {
        if (ctx.context().containsKey(CommonContext.INDEX)) {
            this.index = (String) ctx.context(CommonContext.INDEX);
        } else {
            this.index = this.globalIndex;
        }

        Map<String, Object> metadata = buildSource(ctx);

        IndexRequest req = new IndexRequest(index);
        String id = generateId(uri, ctx);
        req.id(id);
        req.source(metadata);
        add(req);

        return flush(id, false);
    }

    protected Map<String, Object> buildSource(ProcessingContext ctx) {
        Map<String, Object> metadata = new HashMap<>(ctx.metadata());
        return metadata;
    }

    protected String generateId(URI uri, ProcessingContext ctx) {
        return DigestUtils.md5Hex(uri.toString()).toLowerCase();
    }

    protected void add(IndexRequest req) {
        bulkRequest.add(req);
    }

    @Override
    public SinkResponse flush(boolean force) {
        return flush(null, force);
    }

    protected SinkResponse flush(String id, boolean force) {
        int actions = bulkRequest.numberOfActions();
        long bytes = bulkRequest.estimatedSizeInBytes();
        boolean aboveMinimum = flushConfig.isAboveMinimum(actions, bytes);
        boolean aboveMaximum = flushConfig.isAboveMaximum(actions, bytes);
        if (actions > 0 && (force && aboveMinimum) || (!force && aboveMaximum)) {

            try {
                BulkResponse response = flush();
                bulkRequest = new BulkRequest();

                return new ElasticsearchResponse(id, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new EmptyResponse(id);
    }

    private BulkResponse flush() throws IOException {
        int actions = bulkRequest.numberOfActions();
        if (actions > 0) {
            long bytes = bulkRequest.estimatedSizeInBytes();
            String size = SizeUtils.humanReadableByteCount(bytes, true);
            LOGGER.info("Executing bulk request with {} actions and {}.", actions, size);
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } else {
            return null;
        }
    }

    @Override
    public void finish() {
        try {
            flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
//            stopClient();
        }
    }

    private void stopClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.error("Error closing {}.", client.getClass().getSimpleName(), e);
            }
        }
    }

    protected String getIndex() {
        return index;
    }

}
