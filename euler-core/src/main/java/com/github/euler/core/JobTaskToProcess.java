package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobTaskToProcess implements TaskCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<ProcessorCommand> replyTo;
    public final ProcessingContext ctx;

    public JobTaskToProcess(URI uri, URI itemURI, ProcessingContext ctx, ActorRef<ProcessorCommand> replyTo) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.ctx = ctx;
        this.replyTo = replyTo;
    }

    public JobTaskToProcess(JobItemToProcess msg, ActorRef<ProcessorCommand> replyTo) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.replyTo = replyTo;
        this.ctx = msg.ctx;
    }

    public JobTaskToProcess(JobTaskToProcess msg, ActorRef<ProcessorCommand> replyTo) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.replyTo = replyTo;
        this.ctx = msg.ctx;
    }

}
