package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public abstract class AbstractSourceExecution extends AbstractBehavior<SourceCommand> {

    protected JobToScan msg;

    public AbstractSourceExecution(ActorContext<SourceCommand> context) {
        super(context);
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToScan.class, this::onJobToScan);
        return builder.build();
    }

    protected Behavior<SourceCommand> onJobToScan(JobToScan msg) throws IOException {
        if (this.msg == null) {
            this.msg = msg;
            startScan(msg.uri);
        }
        return this;
    }

    protected void startScan(URI uri) throws IOException {
        doScan(msg.uri);
        finish();
    }

    protected abstract void doScan(URI uri) throws IOException;

    protected void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
        msg.replyTo.tell(new JobItemFound(uri, itemURI, ctx));
    }

    protected void finish() {
        msg.replyTo.tell(new ScanFinished(msg));
    }

}
