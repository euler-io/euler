package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public abstract class AbstractSourceExecution extends AbstractBehavior<SourceCommand> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected JobToScan msg;

    public AbstractSourceExecution(ActorContext<SourceCommand> context) {
        super(context);
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToScan.class, this::onJobToScan);
        builder.onMessage(ProcessingStatus.class, this::onProcessingStatus);
        return builder.build();
    }

    protected Behavior<SourceCommand> onJobToScan(JobToScan msg) throws IOException {
        if (this.msg == null) {
            this.msg = msg;
            startScan(msg.uri);
        }
        return this;
    }

    protected Behavior<SourceCommand> onProcessingStatus(ProcessingStatus msg) throws IOException {
        return this;
    }

    protected void startScan(URI uri) throws IOException {
        LOGGER.info("Source scan started.");
        doScan(msg.uri);
        finish();
    }
    
    public boolean isNotifiable() {
        return false;
    }

    protected abstract void doScan(URI uri) throws IOException;

    protected void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
        msg.replyTo.tell(new JobItemFound(uri, itemURI, ctx));
    }

    protected void finish() {
        LOGGER.info("Source scan completed.");
        msg.replyTo.tell(new ScanFinished(msg));
    }

}
