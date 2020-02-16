package com.github.euler.core;

import java.net.URI;

public class JobItemFound implements EulerCommand {

    public final URI uri;
    public final URI itemURI;

    public JobItemFound(URI uri, URI itemURI) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
    }

}
