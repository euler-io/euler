package com.github.euler.tika;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface BatchSink {

    SinkResponse store(URI uri, ProcessingContext ctx);

    SinkResponse storeFragment(String parentId, int index, String fragment);

    SinkResponse flush(boolean force);

    void finish();

}
