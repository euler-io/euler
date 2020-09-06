package com.github.euler.tika;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface MetadataBatchSink {

    SinkResponse store(URI uri, ProcessingContext ctx);

    SinkResponse flush(boolean force);

    void finish();

}
