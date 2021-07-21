package com.github.euler.elasticsearch;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.core.ProcessingContext;
import com.github.euler.elasticsearch.req.InsertRequestFactory;
import com.github.euler.tika.FlushConfig;
import com.github.euler.tika.FragmentBatchSink;
import com.github.euler.tika.SinkResponse;

public class ElasticsearchFragmentSink extends ElasticsearchMetadataSink implements FragmentBatchSink {

    public ElasticsearchFragmentSink(RestHighLevelClient client, String index, FlushConfig flushConfig) {
        super(client, index, flushConfig, new InsertRequestFactory());
    }

    @Override
    protected Map<String, Object> buildSource(URI uri, ProcessingContext ctx) {
        Map<String, Object> metadata = new HashMap<>(super.buildSource(uri, ctx));
        metadata.put("join_field", "item");
        return metadata;
    }

    @Override
    public SinkResponse storeFragment(String parentId, int fragIndex, String fragment) {
        Map<String, Object> data = new HashMap<>();
        data.put("content", fragment);
        data.put("size", fragment.length());
        data.put("fragment-index", fragIndex);

        Map<String, Object> joinField = new HashMap<String, Object>(2);
        joinField.put("name", "fragment");
        joinField.put("parent", parentId);

        data.put("join_field", joinField);

        String fragId = UUID.randomUUID().toString();

        IndexRequest req = new IndexRequest(this.getIndex());
        req.routing(parentId);
        req.id(fragId);
        req.source(data);
        add(req);
        return flush(fragId, false);
    }

}
