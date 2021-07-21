package com.github.euler.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.common.SizeUtils;
import com.github.euler.core.ProcessingContext;
import com.github.euler.elasticsearch.req.ElasticSearchRequestFactory;
import com.github.euler.tika.EmptyResponse;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.MetadataBatchSink;
import com.github.euler.tika.SinkResponse;

public class ElasticsearchMetadataSink implements MetadataBatchSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchMetadataSink.class);

    private final RestHighLevelClient client;
    private final FlushConfig flushConfig;
    private final ElasticSearchRequestFactory<?> requestFactory;

    private BulkRequest bulkRequest;
    private String globalIndex;
    private String index;

    public ElasticsearchMetadataSink(RestHighLevelClient client, String index, FlushConfig flushConfig, ElasticSearchRequestFactory<?> requestFactory) {
        super();
        this.client = client;
        this.globalIndex = index;
        this.flushConfig = flushConfig;
        this.requestFactory = requestFactory;
        this.bulkRequest = new BulkRequest();
    }

    @Override
    public SinkResponse store(URI uri, ProcessingContext ctx) {
        if (ctx.context().containsKey(CommonContext.INDEX)) {
            this.index = (String) ctx.context(CommonContext.INDEX);
        } else {
            this.index = this.globalIndex;
        }

        Map<String, Object> metadata = buildSource(uri, ctx);
        String id = ctx.context(CommonContext.ID, () -> generateId(uri, ctx));

        DocWriteRequest<?> req = requestFactory.create(id, id, metadata);
        add(req);

        return flush(id, false);
    }

    protected Map<String, Object> buildSource(URI uri, ProcessingContext ctx) {
        Map<String, Object> metadata = new HashMap<>(ctx.metadata());
        metadata.put(CommonMetadata.ITEM_URI, uri.normalize().toString());
        return metadata;
    }

    protected String generateId(URI uri, ProcessingContext ctx) {
        return DigestUtils.md5Hex(uri.normalize().toString()).toLowerCase();
    }

    protected void add(DocWriteRequest<?> req) {
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
        }
    }

    protected String getIndex() {
        return index;
    }

}
