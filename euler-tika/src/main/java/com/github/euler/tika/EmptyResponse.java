package com.github.euler.tika;

import java.util.Collections;
import java.util.List;

public class EmptyResponse implements SinkResponse {

    private final String id;

    public EmptyResponse(String id) {
        super();
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<SinkItemResponse> getResponses() {
        return Collections.emptyList();
    }

}
