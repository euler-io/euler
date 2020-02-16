package com.github.euler.command;

import java.net.URI;

public class NoSuitableDiscovererForJob implements JobCommand {

    public final URI uri;

    public NoSuitableDiscovererForJob(URI uri) {
        this.uri = uri;
    }

}
