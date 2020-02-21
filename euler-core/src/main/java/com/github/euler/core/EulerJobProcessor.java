package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerJobProcessor extends AbstractBehavior<EulerCommand> {

    public static Behavior<EulerCommand> create(Behavior<SourceCommand> sourceBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        return Behaviors.setup(ctx -> new EulerJobProcessor(ctx, sourceBehaviour, processorBehavior));
    }

    private Behavior<SourceCommand> sourceBehaviour;
    private ActorRef<SourceCommand> sourceRef;

    private Behavior<ProcessorCommand> processorBehavior;
    private ActorRef<ProcessorCommand> processorRef;

    private final EulerState state;

    public EulerJobProcessor(ActorContext<EulerCommand> ctx, Behavior<SourceCommand> discovererBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        super(ctx);
        this.sourceBehaviour = discovererBehaviour;
        this.processorBehavior = processorBehavior;
        this.state = new EulerState();
        start();
    }

    private void start() {
        sourceRef = getContext().spawn(sourceBehaviour, "euler-source");
        processorRef = getContext().spawn(processorBehavior, "euler-processor");
    }

    @Override
    public Receive<EulerCommand> createReceive() {
        ReceiveBuilder<EulerCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToProcess.class, this::onJobToProcess);
        builder.onMessage(JobItemFound.class, this::onJobItemFound);
        builder.onMessage(JobItemProcessed.class, this::onJobItemProcessed);
        builder.onMessage(ScanFinished.class, this::onScanFinished);
        builder.onMessage(ScanFailed.class, this::onScanFailed);
        builder.onMessage(NoSuitableSource.class, this::onNoSuitableSource);
        return builder.build();
    }

    private Behavior<EulerCommand> onJobToProcess(JobToProcess msg) {
        getContext().getLog().info("{} received to be processed.", msg.uri);
        state.onMessage(msg);
        sourceRef.tell(new JobToScan(msg, getContext().getSelf()));
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobItemFound(JobItemFound msg) {
        processorRef.tell(new JobItemToProcess(msg, state.getCtx(), getContext().getSelf()));
        state.onMessage(msg);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobItemProcessed(JobItemProcessed msg) {
        state.onMessage(msg);
        checkFinished(msg.uri);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onScanFinished(ScanFinished msg) {
        state.onMessage(msg);
        checkFinished(msg.uri);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onScanFailed(ScanFailed msg) {
        state.onMessage(msg);
        checkFinished(msg.uri);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onNoSuitableSource(NoSuitableSource msg) {
        ActorRef<JobCommand> replyTo = state.getReplyTo();
        replyTo.tell(new NoSuitableSourceForJob(msg.uri));
        return Behaviors.same();
    }

    private void checkFinished(URI uri) {
        if (state.isProcessed()) {
            ActorRef<JobCommand> replyTo = state.getReplyTo();
            replyTo.tell(new JobProcessed(uri));
        }
    }

}
