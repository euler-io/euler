package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class SourceExecution extends AbstractSourceExecution {

    public static Behavior<SourceCommand> create(Source source) {
        return Behaviors.setup((context) -> new SourceExecution(context, source));
    }

    private Source source;

    private SourceExecution(ActorContext<SourceCommand> context, Source source) {
        super(context);
        this.source = source;
    }

    @Override
    protected void doScan(URI uri) throws IOException {
        source.scan(uri, listener);
    }

    private final SourceListener listener = new SourceListener() {

        @Override
        public void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
            SourceExecution.this.notifyItemFound(uri, itemURI, ctx);
        }

    };

}
