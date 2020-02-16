package com.github.euler.command;

import java.net.URI;

public class DiscoveryFinished implements EulerCommand {

    public final URI uri;

    public DiscoveryFinished(JobToDiscover msg) {
        this.uri = msg.uri;
    }

    public DiscoveryFinished(URI uri) {
        this.uri = uri;
    }

}
