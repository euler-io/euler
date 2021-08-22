package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import com.github.euler.core.resume.AlwaysResumeStrategy;
import com.github.euler.core.resume.ResumeStrategy;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class PausableSourceExecution extends AbstractSourceExecution {

    public static Behavior<SourceCommand> create(PausableSource source, ResumeStrategy resumeStrategy) {
        return Behaviors.setup((context) -> new PausableSourceExecution(context, source, resumeStrategy));
    }

    public static Behavior<SourceCommand> create(PausableSource source) {
        return Behaviors.setup((context) -> new PausableSourceExecution(context, source, new AlwaysResumeStrategy()));
    }

    private final PausableSource source;
    private final ResumeStrategy resumeStrategy;

    private PausableSourceExecution(ActorContext<SourceCommand> context, PausableSource source, ResumeStrategy resumeStrategy) {
        super(context);
        this.source = source;
        this.resumeStrategy = resumeStrategy;
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToScan.class, this::onJobToScan);
        builder.onMessage(ResumeScan.class, this::onResumeScan);
        builder.onSignal(PostStop.class, this::onPostStop);
        builder.onMessage(ProcessingStatus.class, this::onProcessingStatus);
        return builder.build();
    }

    private Behavior<SourceCommand> onPostStop(PostStop signal) throws IOException {
        source.finishScan();
        return this;
    }

    @Override
    protected void startScan(URI uri) throws IOException {
        prepareScan(uri);
        doScan(uri);
    }

    protected void prepareScan(URI uri) throws IOException {
        source.prepareScan(uri);
    }

    @Override
    protected void doScan(URI uri) throws IOException {
        if (source.doScan(listener)) {
            finish();
        } else {
            yield();
        }
    }

    private Behavior<SourceCommand> onResumeScan(ResumeScan msg) throws IOException {
        if (this.msg != null) {
            doScan(this.msg.uri);
        }
        return this;
    }

    @Override
    protected Behavior<SourceCommand> onProcessingStatus(ProcessingStatus msg) throws IOException {
        if (resumeStrategy.onProcessingStatus(msg)) {
            notifyResumeScan();
        }
        return this;
    }

    protected void yield() {
        if (resumeStrategy.shouldResumeScan()) {
            notifyResumeScan();
        }
    }

    protected void notifyResumeScan() {
        getContext().getSelf().tell(new ResumeScan());
    }

    private static class ResumeScan implements SourceCommand {

    }

    private final SourceListener listener = new SourceListener() {

        @Override
        public void notifyItemFound(URI uri, URI itemURI, ProcessingContext ctx) {
            PausableSourceExecution.this.notifyItemFound(uri, itemURI, ctx);
        }

    };

}
