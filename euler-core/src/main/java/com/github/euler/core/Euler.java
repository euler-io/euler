package com.github.euler.core;

import com.github.euler.command.DiscovererCommand;
import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobItemFound;
import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.JobToDiscover;
import com.github.euler.command.JobToProcess;
import com.github.euler.command.ProcessorCommand;
import com.github.euler.exception.ProcessingAlreadyStarted;

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

//    private final EulerState state;

    public Euler(ActorContext<EulerCommand> ctx, Behavior<DiscovererCommand> discovererBehaviour, Behavior<ProcessorCommand> processorBehavior) {
        super(ctx);
        this.discovererBehaviour = discovererBehaviour;
        this.processorBehavior = processorBehavior;
//        this.state = new EulerState();
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
        return builder.build();
    }

    private Behavior<EulerCommand> onJobToProcess(JobToProcess msg) throws ProcessingAlreadyStarted {
        getContext().getLog().info("{} received to be processed.", msg.uri);
//        state.onMessage(etp);
        discovererRef.tell(new JobToDiscover(msg, getContext().getSelf()));
        return Behaviors.same();
    }

    private Behavior<EulerCommand> onJobItemFound(JobItemFound msg) {
        processorRef.tell(new JobItemToProcess(msg, getContext().getSelf()));
        return Behaviors.same();
    }

}
