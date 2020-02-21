package com.github.euler.core;

import java.net.URI;

public class ScanFinished implements EulerCommand {

    public final URI uri;

    public ScanFinished(JobToScan msg) {
        this.uri = msg.uri;
    }

    public ScanFinished(URI uri) {
        this.uri = uri;
    }

}
