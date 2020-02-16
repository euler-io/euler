package com.github.euler.command;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobToProcess implements EulerCommand {

    public final URI uri;
    public final ActorRef<JobCommand> replyTo;

    public JobToProcess(URI uri, ActorRef<JobCommand> replyTo) {
        this.uri = uri;
        this.replyTo = replyTo;
    }

}
