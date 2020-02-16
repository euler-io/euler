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

public class EulerDiscoverer extends AbstractBehavior<DiscovererCommand> {

    public static Behavior<DiscovererCommand> create(Discoverer... discoverers) {
        return Behaviors.setup(ctx -> new EulerDiscoverer(ctx, discoverers));
    }

    private final Discoverer[] discoverers;

    public EulerDiscoverer(ActorContext<DiscovererCommand> ctx, Discoverer... discoverers) {
        super(ctx);
        this.discoverers = discoverers;
    }

    @Override
    public Receive<DiscovererCommand> createReceive() {
        ReceiveBuilder<DiscovererCommand> builder = newReceiveBuilder();
        builder.onMessage(DiscoveryFailed.class, this::onDiscoveryFailed);
        builder.onMessage(JobToDiscover.class, this::onJobToDiscover);
        return builder.build();
    }

    private Behavior<DiscovererCommand> onDiscoveryFailed(DiscoveryFailed msg) {
        msg.replyTo.tell(new com.github.euler.core.DiscoveryFailed(msg.uri));
        return Behaviors.same();
    }

    private Behavior<DiscovererCommand> onJobToDiscover(JobToDiscover msg) {
        ActorRef<DiscovererCommand> ref = findSuitableDiscoverer(msg);
        if (ref != null) {
            ref.tell(msg);
        } else {
            msg.replyTo.tell(new NoSuitableDiscoverer(msg));
        }
        return Behaviors.same();
    }

    private ActorRef<DiscovererCommand> findSuitableDiscoverer(JobToDiscover msg) {
        for (Discoverer d : this.discoverers) {
            if (d.accepts(msg.uri)) {
                return getActorRef(d, msg);
            }
        }
        return null;
    }

    private ActorRef<DiscovererCommand> getActorRef(Discoverer d, JobToDiscover msg) {
        Behavior<DiscovererCommand> behavior = superviseDiscovererBehavior(d);
        ActorRef<DiscovererCommand> ref = getContext().spawn(behavior, d.name());
        getContext().watchWith(ref, new DiscoveryFailed(msg));
        return ref;
    }

    private Behavior<DiscovererCommand> superviseDiscovererBehavior(Discoverer d) {
        Behavior<DiscovererCommand> behavior = Behaviors.supervise(d.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    private final class DiscoveryFailed implements DiscovererCommand {

        public final URI uri;
        public final ActorRef<EulerCommand> replyTo;

        public DiscoveryFailed(JobToDiscover msg) {
            this.uri = msg.uri;
            this.replyTo = msg.replyTo;
        }

    }

}
