package com.github.euler.elasticsearch.req;

import java.util.Map;

import org.elasticsearch.action.update.UpdateRequest;

public class UpsertRequestFactory extends UpdateRequestFactory {

    @Override
    public UpdateRequest create(String index, String id, Map<String, Object> source) {
        UpdateRequest req = super.create(index, id, source);
        req.docAsUpsert(true);
        return req;
    }

}
