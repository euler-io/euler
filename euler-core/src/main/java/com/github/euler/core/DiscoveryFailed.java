package com.github.euler.core;

import java.net.URI;

public class DiscoveryFailed implements EulerCommand {

    public final URI uri;

    public DiscoveryFailed(URI uri) {
        this.uri = uri;
    }

}
