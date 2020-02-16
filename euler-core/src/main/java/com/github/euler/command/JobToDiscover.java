package com.github.euler.command;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobToDiscover implements DiscovererCommand {

    public final URI uri;
    public final ActorRef<EulerCommand> replyTo;

    public JobToDiscover(URI uri, ActorRef<EulerCommand> replyTo) {
        super();
        this.uri = uri;
        this.replyTo = replyTo;
    }

    public JobToDiscover(JobToProcess msg, ActorRef<EulerCommand> replyTo) {
        this.uri = msg.uri;
        this.replyTo = replyTo;
    }

}
