package com.github.euler.tika;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public class VoidSink implements BatchSink {

    @Override
    public String store(URI uri, ProcessingContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SinkReponse storeFragment(String parentId, String frag) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SinkReponse flush(boolean force) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void finish() {
        // Nothing to do.
    }

}
