package com.github.euler.core;

import java.net.URI;

public final class Item {

    public final URI parentURI;
    public final URI itemURI;
    public final ProcessingContext ctx;

    public Item(URI parentURI, URI itemURI, ProcessingContext ctx) {
        super();
        this.parentURI = parentURI;
        this.itemURI = itemURI;
        this.ctx = ctx;
    }

    public Item(JobTaskToProcess msg) {
        this.parentURI = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = msg.ctx;
    }

}
