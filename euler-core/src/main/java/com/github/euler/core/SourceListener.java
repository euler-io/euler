package com.github.euler.core;

import java.net.URI;

public interface SourceListener {

    void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx);

}
