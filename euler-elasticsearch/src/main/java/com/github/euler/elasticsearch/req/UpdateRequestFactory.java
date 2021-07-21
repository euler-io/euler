package com.github.euler.elasticsearch.req;

import java.util.Map;

import org.elasticsearch.action.update.UpdateRequest;

public class UpdateRequestFactory implements ElasticSearchRequestFactory<UpdateRequest> {

    @Override
    public UpdateRequest create(String index, String id, Map<String, Object> source) {
        UpdateRequest req = new UpdateRequest(index, id);
        req.doc(source);
        return req;
    }

}
