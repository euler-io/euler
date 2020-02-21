package com.github.euler.core;

import java.net.URI;

public class NoSuitableSourceForJob implements JobCommand {

    public final URI uri;

    public NoSuitableSourceForJob(URI uri) {
        this.uri = uri;
    }

}
