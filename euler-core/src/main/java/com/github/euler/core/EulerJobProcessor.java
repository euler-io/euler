package com.github.euler.core;

import java.net.URI;
import java.time.Duration;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerJobProcessor extends AbstractBehavior<EulerCommand> {

    public static Behavior<EulerCommand> create(Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior) {
        return Behaviors.setup(ctx -> new EulerJobProcessor(ctx, sourceBehavior, processorBehavior));
    }

    private Behavior<SourceCommand> sourceBehaviour;
    private ActorRef<SourceCommand> sourceRef;

    private Behavior<ProcessorCommand> processorBehavior;
    private ActorRef<ProcessorCommand> processorRef;

    private final EulerState state;
    private Cancellable flusher;

    private EulerJobProcessor(ActorContext<EulerCommand> ctx, Behavior<SourceCommand> sourceBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        super(ctx);
        this.sourceBehaviour = sourceBehaviour;
        this.processorBehavior = processorBehavior;
        this.state = new EulerState();
        this.flusher = null;

        start();
    }

    private void start() {
        processorRef = getContext().spawn(supervisedProcessorBehavior(), "euler-processor");
        getContext().watchWith(processorRef, new InternalProcessorFailed());
    }

    protected Behavior<ProcessorCommand> supervisedProcessorBehavior() {
        return Behaviors.supervise(this.processorBehavior).onFailure(SupervisorStrategy.stop());
    }

    @Override
    public Receive<EulerCommand> createReceive() {
        ReceiveBuilder<EulerCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToProcess.class, this::onJobToProcess);
        builder.onMessage(JobItemFound.class, this::onJobItemFound);
        builder.onMessage(JobEmbeddedItemFound.class, this::onJobEmbeddedItemFound);
        builder.onMessage(JobItemProcessed.class, this::onJobItemProcessed);
        builder.onMessage(ScanFinished.class, this::onScanFinished);
        builder.onMessage(ScanFailed.class, this::onScanFailed);
        builder.onMessage(InternalProcessorFailed.class, this::onInternalProcessorFailed);
        return builder.build();
    }

    private Behavior<EulerCommand> onJobToProcess(JobToProcess msg) {
        getContext().getLog().info("{} received to be processed.", msg.uri);
        state.onMessage(msg);
        spawnSourceRef(msg).tell(new JobToScan(msg, getContext().getSelf()));
        return Behaviors.same();
    }

    private ActorRef<SourceCommand> spawnSourceRef(JobToProcess msg) {
        this.sourceRef = getContext().spawn(supervisedSourceBehavior(), "euler-source");
        getContext().watchWith(sourceRef, new ScanFailed(msg.uri));
        return sourceRef;
    }

    private Behavior<SourceCommand> supervisedSourceBehavior() {
        return Behaviors.supervise(this.sourceBehaviour).onFailure(SupervisorStrategy.stop());
    }

    private Behavior<EulerCommand> onJobItemFound(JobItemFound msg) {
        ProcessingContext ctx = state.getCtx().merge(msg.ctx);
        processorRef.tell(new JobItemToProcess(msg, ctx, getContext().getSelf()));
        state.onMessage(msg);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobEmbeddedItemFound(JobEmbeddedItemFound msg) {
        ProcessingContext ctx = state.getCtx().merge(msg.ctx);
        processorRef.tell(new JobItemToProcess(msg, ctx, getContext().getSelf()));
        state.onMessage(msg);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobItemProcessed(JobItemProcessed msg) {
        state.onMessage(msg);
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> onScanFinished(ScanFinished msg) {
        state.onMessage(msg);
        startFlush();
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> onScanFailed(ScanFailed msg) {
        getContext().getLog().warn("Scan of uri {} failed.", msg.uri);
        state.onMessage(msg);
        startFlush();
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> onInternalProcessorFailed(InternalProcessorFailed msg) {
        getContext().getLog().warn("Processor failed.");
        return Behaviors.stopped();
    }

    private void startFlush() {
        processorRef.tell(new Flush(true));
        Duration duration = Duration.ofSeconds(2);
        this.flusher = getContext().getSystem().scheduler().scheduleAtFixedRate(duration, duration, new Runnable() {

            @Override
            public void run() {
                processorRef.tell(new Flush(true));
            }

        }, getContext().getExecutionContext());
    }

    private Behavior<EulerCommand> checkFinished(URI uri) {
        if (state.isProcessed()) {
            ActorRef<JobCommand> replyTo = state.getReplyTo();
            replyTo.tell(new JobProcessed(uri));
            if (flusher != null) {
                flusher.cancel();
            }
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

    private class InternalProcessorFailed implements EulerCommand {

    }

}
