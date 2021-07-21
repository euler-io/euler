package com.github.euler.elasticsearch.req;

import java.util.Map;

import org.elasticsearch.action.DocWriteRequest;

public interface ElasticSearchRequestFactory<T extends DocWriteRequest<T>> {

    T create(String index, String id, Map<String, Object> source);

}
