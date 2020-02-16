package com.github.euler.command;

import java.net.URI;

public class NoSuitableDiscoverer implements EulerCommand {

    public final URI uri;

    public NoSuitableDiscoverer(JobToDiscover msg) {
        this.uri = msg.uri;
    }

}