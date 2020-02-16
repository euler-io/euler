package com.github.euler.testing;

import com.github.euler.command.DiscovererCommand;
import com.github.euler.command.JobToDiscover;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class WillFailDiscovererBehavior extends AbstractBehavior<DiscovererCommand> {

    public static Behavior<DiscovererCommand> create() {
        return Behaviors.setup(ctx -> new WillFailDiscovererBehavior(ctx));
    }

    public WillFailDiscovererBehavior(ActorContext<DiscovererCommand> context) {
        super(context);
    }

    @Override
    public Receive<DiscovererCommand> createReceive() {
        ReceiveBuilder<DiscovererCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToDiscover.class, this::onJobToDiscover);
        return builder.build();
    }

    private Behavior<DiscovererCommand> onJobToDiscover(JobToDiscover msg) {
        throw new RuntimeException("I am expected to fail.");
    }

}
