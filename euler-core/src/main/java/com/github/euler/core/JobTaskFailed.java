package com.github.euler.core;

import java.net.URI;

public class JobTaskFailed implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;

    public JobTaskFailed(URI uri, URI itemURI) {
        this.uri = uri;
        this.itemURI = itemURI;
    }

    public JobTaskFailed(JobItemToProcess msg) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
    }

}