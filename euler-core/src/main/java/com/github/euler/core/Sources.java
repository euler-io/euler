package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Sources {

    private Sources() {
        super();
    }

    public static Behavior<SourceCommand> emptyBehavior() {
        return Behaviors.receive(SourceCommand.class)
                .onMessage(JobToScan.class, (msg) -> {
                    msg.replyTo.tell(new ScanFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<SourceCommand> fixedItemBehavior(URI itemURI) {
        return fixedItemBehavior(itemURI, ProcessingContext.EMPTY);
    }

    public static Behavior<SourceCommand> fixedItemBehavior(URI itemURI, ProcessingContext ctx) {
        return Behaviors.receive(SourceCommand.class)
                .onMessage(JobToScan.class, (msg) -> {
                    msg.replyTo.tell(new JobItemFound(msg.uri, itemURI, ctx));
                    msg.replyTo.tell(new ScanFinished(msg));
                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<SourceCommand> fowardBehavior(ActorRef<SourceCommand> ref) {
        return Behaviors.receive(SourceCommand.class)
                .onAnyMessage((msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }

}
