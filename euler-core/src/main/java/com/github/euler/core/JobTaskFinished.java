package com.github.euler.core;

import java.net.URI;

public class JobTaskFinished implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final ProcessingContext ctx;

    public JobTaskFinished(URI uri, URI itemURI, ProcessingContext ctx) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ctx;
    }

    public JobTaskFinished(JobTaskToProcess msg, ProcessingContext ctx) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = ctx;
    }

}
