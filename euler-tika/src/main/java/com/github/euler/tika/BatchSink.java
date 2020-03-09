package com.github.euler.tika;

import java.net.URI;
import java.util.List;

import com.github.euler.core.ProcessingContext;

public interface BatchSink {

    void finish();

    String store(URI uri, ProcessingContext ctx);

    List<SinkReponse> storeFragment(String parentId, String fragId, int index, String fragment);

    List<SinkReponse> flush(boolean force);

}
