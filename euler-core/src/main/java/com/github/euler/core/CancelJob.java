package com.github.euler.core;

import akka.actor.typed.ActorRef;

public class CancelJob implements JobCommand {

    public final ActorRef<JobCommand> replyTo;

    public CancelJob(ActorRef<JobCommand> replyTo) {
        super();
        this.replyTo = replyTo;
    }

}
