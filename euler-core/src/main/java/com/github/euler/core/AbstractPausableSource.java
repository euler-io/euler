package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

public abstract class AbstractPausableSource implements Source, PausableSource {

    @Override
    public void scan(URI uri, SourceListener listener) throws IOException {
        prepareScan(uri);
        boolean finished = false;
        while (!finished) {
            finished = doScan(listener);
        }
        finishScan();
    }

}
