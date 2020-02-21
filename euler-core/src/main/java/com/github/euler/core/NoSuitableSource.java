package com.github.euler.core;

import java.net.URI;

public class NoSuitableSource implements EulerCommand {

    public final URI uri;

    public NoSuitableSource(JobToScan msg) {
        this.uri = msg.uri;
    }

    public NoSuitableSource(URI uri) {
        this.uri = uri;
    }

}