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

public class EulerSource extends AbstractBehavior<SourceCommand> {

    public static Behavior<SourceCommand> create(Source... discoverers) {
        return Behaviors.setup(ctx -> new EulerSource(ctx, discoverers));
    }

    private final Source[] discoverers;

    public EulerSource(ActorContext<SourceCommand> ctx, Source... discoverers) {
        super(ctx);
        this.discoverers = discoverers;
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(ScanFailed.class, this::onDiscoveryFailed);
        builder.onMessage(JobToScan.class, this::onJobToDiscover);
        return builder.build();
    }

    private Behavior<SourceCommand> onDiscoveryFailed(ScanFailed msg) {
        msg.replyTo.tell(new com.github.euler.core.ScanFailed(msg.uri));
        return Behaviors.same();
    }

    private Behavior<SourceCommand> onJobToDiscover(JobToScan msg) {
        ActorRef<SourceCommand> ref = findSuitableDiscoverer(msg);
        if (ref != null) {
            ref.tell(msg);
        } else {
            msg.replyTo.tell(new NoSuitableSource(msg));
        }
        return Behaviors.same();
    }

    private ActorRef<SourceCommand> findSuitableDiscoverer(JobToScan msg) {
        for (Source d : this.discoverers) {
            if (d.accepts(msg.uri)) {
                return getActorRef(d, msg);
            }
        }
        return null;
    }

    private ActorRef<SourceCommand> getActorRef(Source d, JobToScan msg) {
        Behavior<SourceCommand> behavior = superviseDiscovererBehavior(d);
        ActorRef<SourceCommand> ref = getContext().spawn(behavior, d.name());
        getContext().watchWith(ref, new ScanFailed(msg));
        return ref;
    }

    private Behavior<SourceCommand> superviseDiscovererBehavior(Source d) {
        Behavior<SourceCommand> behavior = Behaviors.supervise(d.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    private final class ScanFailed implements SourceCommand {

        public final URI uri;
        public final ActorRef<EulerCommand> replyTo;

        public ScanFailed(JobToScan msg) {
            this.uri = msg.uri;
            this.replyTo = msg.replyTo;
        }

    }

}
