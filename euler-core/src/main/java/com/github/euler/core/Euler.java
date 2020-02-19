package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class Euler extends AbstractBehavior<EulerCommand> {

    public static Behavior<EulerCommand> create(Behavior<DiscovererCommand> discovererBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        return Behaviors.setup(ctx -> new Euler(ctx, discovererBehaviour, processorBehavior));
    }

    private Behavior<DiscovererCommand> discovererBehaviour;
    private ActorRef<DiscovererCommand> discovererRef;

    private Behavior<ProcessorCommand> processorBehavior;
    private ActorRef<ProcessorCommand> processorRef;

    private final EulerState state;

    public Euler(ActorContext<EulerCommand> ctx, Behavior<DiscovererCommand> discovererBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        super(ctx);
        this.discovererBehaviour = discovererBehaviour;
        this.processorBehavior = processorBehavior;
        this.state = new EulerState();
        start();
    }

    private void start() {
        discovererRef = getContext().spawn(discovererBehaviour, "euler-discoverer");
        processorRef = getContext().spawn(processorBehavior, "euler-processor");
    }

    @Override
    public Receive<EulerCommand> createReceive() {
        ReceiveBuilder<EulerCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToProcess.class, this::onJobToProcess);
        builder.onMessage(JobItemFound.class, this::onJobItemFound);
        builder.onMessage(JobItemProcessed.class, this::onJobItemProcessed);
        builder.onMessage(DiscoveryFinished.class, this::onDiscoveryFinished);
        builder.onMessage(DiscoveryFailed.class, this::onDiscoveryFailed);
        builder.onMessage(NoSuitableDiscoverer.class, this::onNoSuitableDiscoverer);
        return builder.build();
    }

    private Behavior<EulerCommand> onJobToProcess(JobToProcess msg) {
        getContext().getLog().info("{} received to be processed.", msg.uri);
        state.onMessage(msg);
        discovererRef.tell(new JobToDiscover(msg, getContext().getSelf()));
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

    private Behavior<EulerCommand> onDiscoveryFinished(DiscoveryFinished msg) {
        state.onMessage(msg);
        checkFinished(msg.uri);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onDiscoveryFailed(DiscoveryFailed msg) {
        state.onMessage(msg);
        checkFinished(msg.uri);
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onNoSuitableDiscoverer(NoSuitableDiscoverer msg) {
        ActorRef<JobCommand> replyTo = state.getReplyTo();
        replyTo.tell(new NoSuitableDiscovererForJob(msg.uri));
        return Behaviors.same();
    }

    private void checkFinished(URI uri) {
        if (state.isProcessed()) {
            ActorRef<JobCommand> replyTo = state.getReplyTo();
            replyTo.tell(new JobProcessed(uri));
        }
    }

}
