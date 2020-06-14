package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

public interface PausableSource {

    default void prepareScan(URI uri) throws IOException {

    }

    boolean doScan(SourceListener listener) throws IOException;

    default void finishScan() throws IOException {

    }

}
