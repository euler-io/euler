package com.github.euler.command;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class JobTaskToProcess implements TaskCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<ProcessorCommand> replyTo;

    public JobTaskToProcess(JobItemToProcess msg, ActorRef<ProcessorCommand> replyTo) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.replyTo = replyTo;
    }

}
