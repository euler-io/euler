package com.github.euler.core;

import com.github.euler.exception.ProcessingAlreadyStarted;
import com.github.euler.message.EvidenceDiscovery;
import com.github.euler.message.EvidenceItemFound;
import com.github.euler.message.EvidenceItemToProcess;
import com.github.euler.message.EvidenceMessage;
import com.github.euler.message.EvidenceToDiscover;
import com.github.euler.message.EvidenceToProcess;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class Euler extends AbstractBehavior<EvidenceMessage> {

    public static Behavior<EvidenceMessage> create(Behavior<EvidenceDiscovery> discovererBehaviour, Behavior<EvidenceItemToProcess> processorBehavior) {
        return Behaviors.setup(ctx -> new Euler(ctx, discovererBehaviour, processorBehavior));
    }

    private Behavior<EvidenceDiscovery> discovererBehaviour;
    private ActorRef<EvidenceDiscovery> discovererRef;

    private Behavior<EvidenceItemToProcess> processorBehavior;
    private ActorRef<EvidenceItemToProcess> processorRef;

    private final EulerState state;

    public Euler(ActorContext<EvidenceMessage> ctx, Behavior<EvidenceDiscovery> discovererBehaviour, Behavior<EvidenceItemToProcess> processorBehavior) {
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
    public Receive<EvidenceMessage> createReceive() {
        ReceiveBuilder<EvidenceMessage> builder = newReceiveBuilder();
        builder.onMessage(EvidenceToProcess.class, this::onEvidenceToProcess);
        builder.onMessage(EvidenceItemFound.class, this::onEvidenceItemFound);
        return builder.build();
    }

    private Behavior<EvidenceMessage> onEvidenceToProcess(EvidenceToProcess etp) throws ProcessingAlreadyStarted {
        getContext().getLog().info("{} received to be processed.", etp.evidenceURI);
        state.onMessage(etp);
        discovererRef.tell(new EvidenceToDiscover(etp, getContext().getSelf()));
        return Behaviors.same();
    }

    private Behavior<EvidenceMessage> onEvidenceItemFound(EvidenceItemFound eif) {
        processorRef.tell(new EvidenceItemToProcess(eif, getContext().getSelf()));
        return Behaviors.same();
    }

}
