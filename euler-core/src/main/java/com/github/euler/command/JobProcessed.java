package com.github.euler.command;

import java.net.URI;

public class JobProcessed implements JobCommand {

    public final URI uri;

    public JobProcessed(URI uri) {
        super();
        this.uri = uri;
    }

}
