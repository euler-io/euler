package com.github.euler.command;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobItemFound implements EulerCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<EulerCommand> replyTo;

    public JobItemFound(URI uri, URI itemURI, ActorRef<EulerCommand> replyTo) {
        this.uri = uri;
        this.itemURI = itemURI;
        this.replyTo = replyTo;
    }

}
