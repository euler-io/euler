package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobItemToProcess implements ProcessorCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<EulerCommand> replyTo;

    public JobItemToProcess(JobItemFound msg, ActorRef<EulerCommand> replyTo) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.replyTo = replyTo;
    }

    public JobItemToProcess(URI uri, URI itemURI, ActorRef<EulerCommand> replyTo) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.replyTo = replyTo;
    }

}
