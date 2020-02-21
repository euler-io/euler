package com.github.euler.core;

import java.net.URI;

public class ScanFailed implements EulerCommand {

    public final URI uri;

    public ScanFailed(URI uri) {
        this.uri = uri;
    }

}
