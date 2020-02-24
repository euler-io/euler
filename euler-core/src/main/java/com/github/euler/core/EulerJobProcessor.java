package com.github.euler.core;

import java.net.URI;

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
        return builder.build();
    }

    private Behavior<EulerCommand> onJobToProcess(JobToProcess msg) {
        getContext().getLog().info("{} received to be processed.", msg.uri);
        state.onMessage(msg);
        getSourceRef(msg).tell(new JobToScan(msg, getContext().getSelf()));
        return Behaviors.same();
    }

    private ActorRef<SourceCommand> getSourceRef(JobToProcess msg) {
        ActorRef<SourceCommand> sourceRef = getContext().spawn(supervisedSourceBehavior(), "euler-source");
        getContext().watchWith(sourceRef, new ScanFailed(msg.uri));
        return sourceRef;
    }

    private Behavior<SourceCommand> supervisedSourceBehavior() {
        return Behaviors.supervise(this.sourceBehaviour).onFailure(SupervisorStrategy.stop());
    }

    private Behavior<EulerCommand> onJobItemFound(JobItemFound msg) {
        processorRef.tell(new JobItemToProcess(msg, state.getCtx(), getContext().getSelf()));
        state.onMessage(msg);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobItemProcessed(JobItemProcessed msg) {
        state.onMessage(msg);
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> onScanFinished(ScanFinished msg) {
        state.onMessage(msg);
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> onScanFailed(ScanFailed msg) {
        state.onMessage(msg);
        return checkFinished(msg.uri);
    }

    private Behavior<EulerCommand> checkFinished(URI uri) {
        if (state.isProcessed()) {
            ActorRef<JobCommand> replyTo = state.getReplyTo();
            replyTo.tell(new JobProcessed(uri));
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

}
