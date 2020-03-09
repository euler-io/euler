package com.github.euler.elasticsearch;

import org.elasticsearch.action.bulk.BulkItemResponse;

import com.github.euler.tika.SinkReponse;

public class ElasticSearchResponse implements SinkReponse {

    private BulkItemResponse item;

    public ElasticSearchResponse(BulkItemResponse item) {
        this.item = item;
    }

    @Override
    public String getId() {
        if (item.isFailed()) {
            return item.getResponse().getId();
        } else {
            return item.getFailure().getId();
        }
    }

    @Override
    public boolean isFailed() {
        return item.isFailed();
    }

    @Override
    public Exception getFailureCause() {
        if (item.isFailed()) {
            return item.getFailure().getCause();
        } else {
            return null;
        }
    }

}
