package com.github.euler.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.github.euler.tika.BatchSink;
import com.github.euler.tika.EmptyResponse;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.SinkResponse;

public class ElasticSearchSink implements BatchSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSink.class);

    private final RestHighLevelClient client;
    private final FlushConfig flushConfig;

    private BulkRequest bulkRequest;
    private String globalIndex;
    private String index;

    public ElasticSearchSink(RestHighLevelClient client, String index, FlushConfig flushConfig) {
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

        Map<String, Object> metadata = new HashMap<>(ctx.metadata());
        metadata.put("join_field", "item");

        IndexRequest req = new IndexRequest(index);
        String id = generateId(uri, ctx);
        req.id(id);
        req.source(metadata);
        add(req);

        return flush(id, false);
    }

    protected String generateId(URI uri, ProcessingContext ctx) {
        return DigestUtils.md5Hex(uri.toString()).toLowerCase();
    }

    @Override
    public SinkResponse storeFragment(String parentId, int fragIndex, String fragment) {
        Map<String, Object> data = new HashMap<>();
        data.put("content", fragment);
        data.put("size", fragment.length());
        data.put("index", fragIndex);

        Map<String, Object> joinField = new HashMap<String, Object>(1);
        joinField.put("name", "fragment");
        joinField.put("parent", parentId);

        data.put("join_field", joinField);

        String fragId = UUID.randomUUID().toString();

        IndexRequest req = new IndexRequest(this.index);
        req.routing(parentId);
        req.id(fragId);
        req.source(data);
        add(req);
        return flush(fragId, false);
    }

    private void add(IndexRequest req) {
        bulkRequest.add(req);
    }

    @Override
    public SinkResponse flush(boolean force) {
        return flush(null, force);
    }

    private SinkResponse flush(String id, boolean force) {
        int actions = bulkRequest.numberOfActions();
        long bytes = bulkRequest.estimatedSizeInBytes();
        boolean aboveMinimum = flushConfig.isAboveMinimum(actions, bytes);
        boolean aboveMaximum = flushConfig.isAboveMaximum(actions, bytes);
        if ((force && aboveMinimum) || (!force && aboveMaximum)) {
            String size = SizeUtils.humanReadableByteCount(bytes, true);
            LOGGER.info("Executing bulk request with {} actions and {}.", actions, size);

            try {
                BulkResponse response = flush();
                bulkRequest = new BulkRequest();

                return new ElasticSearchResponse(id, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new EmptyResponse(id);
    }

    private BulkResponse flush() throws IOException {
        int actions = bulkRequest.numberOfActions();
        long bytes = bulkRequest.estimatedSizeInBytes();
        String size = SizeUtils.humanReadableByteCount(bytes, true);
        LOGGER.info("Executing bulk request with {} actions and {}.", actions, size);
        return client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void finish() {
        try {
            flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            stopClient();
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

}
