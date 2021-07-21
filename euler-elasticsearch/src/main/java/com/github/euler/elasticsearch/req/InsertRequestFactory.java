package com.github.euler.elasticsearch.req;

import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;

public class InsertRequestFactory implements ElasticSearchRequestFactory<IndexRequest> {

    @Override
    public IndexRequest create(String index, String id, Map<String, Object> source) {
        IndexRequest req = new IndexRequest(index);
        req.id(id);
        req.source(source);
        return req;
    }

}
