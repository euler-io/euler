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

    public static Behavior<SourceCommand> create(Source... sources) {
        return Behaviors.setup(ctx -> new EulerSource(ctx, sources));
    }

    private final Source[] sources;

    public EulerSource(ActorContext<SourceCommand> ctx, Source... sources) {
        super(ctx);
        this.sources = sources;
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(InternalScanFailed.class, this::onScanFailed);
        builder.onMessage(JobToScan.class, this::onJobToScan);
        return builder.build();
    }

    private Behavior<SourceCommand> onScanFailed(InternalScanFailed msg) {
        msg.replyTo.tell(new ScanFailed(msg.uri));
        return Behaviors.same();
    }

    private Behavior<SourceCommand> onJobToScan(JobToScan msg) {
        ActorRef<SourceCommand> ref = findSuitableSource(msg);
        if (ref != null) {
            ref.tell(msg);
        } else {
            msg.replyTo.tell(new NoSuitableSource(msg));
        }
        return Behaviors.same();
    }

    private ActorRef<SourceCommand> findSuitableSource(JobToScan msg) {
        for (Source d : this.sources) {
            if (d.accepts(msg.uri)) {
                return getActorRef(d, msg);
            }
        }
        return null;
    }

    private ActorRef<SourceCommand> getActorRef(Source d, JobToScan msg) {
        Behavior<SourceCommand> behavior = superviseDiscovererBehavior(d);
        ActorRef<SourceCommand> ref = getContext().spawn(behavior, d.name());
        getContext().watchWith(ref, new InternalScanFailed(msg));
        return ref;
    }

    private Behavior<SourceCommand> superviseDiscovererBehavior(Source d) {
        Behavior<SourceCommand> behavior = Behaviors.supervise(d.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    private final class InternalScanFailed implements SourceCommand {

        public final URI uri;
        public final ActorRef<EulerCommand> replyTo;

        public InternalScanFailed(JobToScan msg) {
            this.uri = msg.uri;
            this.replyTo = msg.replyTo;
        }

    }

}
