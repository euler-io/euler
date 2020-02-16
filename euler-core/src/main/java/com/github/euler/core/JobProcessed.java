package com.github.euler.core;

import java.net.URI;

public class JobProcessed implements JobCommand {

    public final URI uri;

    public JobProcessed(URI uri) {
        super();
        this.uri = uri;
    }

}
