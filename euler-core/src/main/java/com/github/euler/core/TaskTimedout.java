package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;

public class TaskTimedout implements TaskCommand {

    public final URI uri;
    public final URI itemURI;
    public final ActorRef<ProcessorCommand> replyTo;
    public final ProcessingContext ctx;
    public final String taskName;

    public TaskTimedout(URI uri, URI itemURI, ActorRef<ProcessorCommand> replyTo, ProcessingContext ctx, String taskName) {
        super();
        this.uri = uri;
        this.itemURI = itemURI;
        this.replyTo = replyTo;
        this.ctx = ctx;
        this.taskName = taskName;
    }

    public TaskTimedout(JobTaskToProcess msg, String taskName) {
        this.uri = msg.uri;
        this.itemURI = msg.itemURI;
        this.replyTo = msg.replyTo;
        this.ctx = ProcessingContext.EMPTY;
        this.taskName = taskName;
    }

}
