package com.github.euler.core;

import java.net.URI;

public class EmbeddedItemFound implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final URI parentURI;
    public final ProcessingContext ctx;

    public EmbeddedItemFound(URI itemURI, JobTaskToProcess msg, ProcessingContext ctx) {
        this.uri = msg.uri;
        this.itemURI = itemURI;
        this.parentURI = msg.itemURI;
        this.ctx = ctx;
    }

}
