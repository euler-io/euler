package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobToScan implements SourceCommand {

    public final URI uri;
    public final ActorRef<EulerCommand> replyTo;

    public JobToScan(URI uri, ActorRef<EulerCommand> replyTo) {
        super();
        this.uri = uri;
        this.replyTo = replyTo;
    }

    public JobToScan(JobToProcess msg, ActorRef<EulerCommand> replyTo) {
        this.uri = msg.uri;
        this.replyTo = replyTo;
    }

}
