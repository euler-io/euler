package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public abstract class CancellableSource extends AbstractBehavior<SourceCommand> {

    private JobToScan msg;

    public CancellableSource(ActorContext<SourceCommand> context) {
        super(context);
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToScan.class, this::onJobToScan);
        builder.onMessage(ResumeScan.class, this::onResumeScan);
        return builder.build();
    }

    private Behavior<SourceCommand> onJobToScan(JobToScan msg) throws IOException {
        if (this.msg == null) {
            this.msg = msg;
            prepareScan(msg.uri);
            startScan();
        }
        return this;
    }

    private void startScan() throws IOException {
        if (doScan(msg.uri)) {
            finish();
        }
    }

    protected void prepareScan(URI uri) {

    }

    private Behavior<SourceCommand> onResumeScan(ResumeScan msg) throws IOException {
        if (msg != null) {
            startScan();
        }
        return this;
    }

    protected void yield() {
        getContext().getSelf().tell(new ResumeScan());
    }

    protected void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
        msg.replyTo.tell(new JobItemFound(uri, itemURI, ctx));
    }

    private void finish() {
        msg.replyTo.tell(new ScanFinished(msg));
    }

    protected abstract boolean doScan(URI uri) throws IOException;

    private static class ResumeScan implements SourceCommand {

    }

}
