package com.github.euler.core;

import java.net.URI;

public class EmbeddedItemFound implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final ProcessingContext ctx;

    public EmbeddedItemFound(JobTaskToProcess msg, ProcessingContext ctx) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = ctx;
    }

}
