package com.github.euler.core;

import java.net.URI;

public class JobItemFound implements EulerCommand {

    public final URI uri;
    public final URI itemURI;
    public final ProcessingContext ctx;

    public JobItemFound(URI uri, URI itemURI) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ProcessingContext.EMPTY;
    }

    public JobItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ctx;
    }

}
