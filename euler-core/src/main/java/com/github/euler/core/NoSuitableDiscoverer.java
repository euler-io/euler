package com.github.euler.core;

import java.net.URI;

public class NoSuitableDiscoverer implements EulerCommand {

    public final URI uri;

    public NoSuitableDiscoverer(JobToDiscover msg) {
        this.uri = msg.uri;
    }

    public NoSuitableDiscoverer(URI uri) {
        this.uri = uri;
    }

}