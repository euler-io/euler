package com.github.euler.core;

import java.net.URI;

public class JobItemProcessed implements EulerCommand {

    public final URI uri;
    public final URI itemURI;

    public JobItemProcessed(URI uri, URI itemURI) {
        this.uri = uri;
        this.itemURI = itemURI;
    }

}
