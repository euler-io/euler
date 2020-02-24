package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class Job implements JobCommand {

    public final URI uri;
    public final ActorRef<JobCommand> replyTo;

    public Job(URI uri, ActorRef<JobCommand> replyTo) {
        this.uri = uri;
        this.replyTo = replyTo;
    }

}
