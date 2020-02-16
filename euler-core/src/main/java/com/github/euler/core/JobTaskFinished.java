package com.github.euler.core;

import java.net.URI;

public class JobTaskFinished implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;

    public JobTaskFinished(JobTaskToProcess msg) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
    }

}
