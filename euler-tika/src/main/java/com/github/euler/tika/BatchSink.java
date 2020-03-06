package com.github.euler.tika;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface BatchSink {

    void finish();

    String store(URI uri, ProcessingContext ctx);

    SinkReponse storeFragment(String parentId, String frag);

    SinkReponse flush(boolean force);

}
