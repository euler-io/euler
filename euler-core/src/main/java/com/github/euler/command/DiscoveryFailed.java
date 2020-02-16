package com.github.euler.command;

import java.net.URI;

public class DiscoveryFailed implements EulerCommand {

    public final URI uri;

    public DiscoveryFailed(URI uri) {
        this.uri = uri;
    }

}
