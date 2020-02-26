package com.github.euler.file;

import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface SourceListener {

    void itemFound(URI uri, URI itemURI, ProcessingContext ctx);

}
