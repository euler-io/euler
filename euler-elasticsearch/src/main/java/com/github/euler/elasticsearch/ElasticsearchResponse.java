package com.github.euler.elasticsearch;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkResponse;

import com.github.euler.tika.SinkItemResponse;
import com.github.euler.tika.SinkResponse;

public class ElasticsearchResponse implements SinkResponse {

    private final String id;
    private final BulkResponse response;

    public ElasticsearchResponse(String id, BulkResponse response) {
        this.id = id;
        this.response = response;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<SinkItemResponse> getResponses() {
        return Arrays.stream(response.getItems())
                .map((item) -> new ElasticsearchItemResponse(item))
                .collect(Collectors.toList());
    }

}
