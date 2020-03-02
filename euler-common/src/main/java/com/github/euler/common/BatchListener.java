package com.github.euler.common;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface BatchListener {

    void finished(URI itemURI, ProcessingContext ctx);

    void failed(URI itemURI, ProcessingContext empty);

}
