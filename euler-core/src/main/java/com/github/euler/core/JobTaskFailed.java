package com.github.euler.core;

import java.net.URI;

public class JobTaskFailed implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final ProcessingContext ctx;

    public JobTaskFailed(URI uri, URI itemURI) {
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ProcessingContext.EMPTY;
    }

    public JobTaskFailed(JobItemToProcess msg) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = ProcessingContext.EMPTY;
    }

    public JobTaskFailed(JobTaskToProcess msg, ProcessingContext ctx) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = ctx;
    }

    public JobTaskFailed(URI uri, URI itemURI, ProcessingContext ctx) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ctx;
    }

}