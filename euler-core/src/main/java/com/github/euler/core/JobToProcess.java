package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobToProcess implements EulerCommand {

    public final URI uri;
    public final ActorRef<JobCommand> replyTo;
    public final ProcessingContext ctx;

    public JobToProcess(URI uri, ActorRef<JobCommand> replyTo) {
        this.uri = uri;
        this.ctx = ProcessingContext.EMPTY;
        this.replyTo = replyTo;
    }

    public JobToProcess(URI uri, ProcessingContext ctx, ActorRef<JobCommand> replyTo) {
        this.uri = uri;
        this.ctx = ctx;
        this.replyTo = replyTo;
    }

}
