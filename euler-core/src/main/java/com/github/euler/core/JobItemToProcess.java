package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobItemToProcess implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<EulerCommand> replyTo;
    public final ProcessingContext ctx;

    public JobItemToProcess(JobItemFound msg, ProcessingContext ctx, ActorRef<EulerCommand> replyTo) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.ctx = ctx;
        this.replyTo = replyTo;
    }

    public JobItemToProcess(URI uri, URI itemURI, ActorRef<EulerCommand> replyTo) {
        this.uri = uri;
        this.itemURI = itemURI;
        this.replyTo = replyTo;
        this.ctx = ProcessingContext.EMPTY;
    }

    public JobItemToProcess(URI uri, URI itemURI, ProcessingContext ctx, ActorRef<EulerCommand> replyTo) {
        this.uri = uri;
        this.itemURI = itemURI;
        this.replyTo = replyTo;
        this.ctx = ctx;
    }

}
