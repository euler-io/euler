package com.github.euler.core;

import com.github.euler.message.EvidenceDiscovery;
import com.github.euler.message.EvidenceDiscoveryFailed;
import com.github.euler.message.EvidenceToDiscover;
import com.github.euler.message.NoSuitableDiscoverer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerDiscoverer extends AbstractBehavior<EvidenceDiscovery> {

    public static Behavior<EvidenceDiscovery> create(Discoverer... discoverers) {
        return Behaviors.setup(ctx -> new EulerDiscoverer(ctx, discoverers));
    }

    private final Discoverer[] discoverers;

    public EulerDiscoverer(ActorContext<EvidenceDiscovery> ctx, Discoverer... discoverers) {
        super(ctx);
        this.discoverers = discoverers;
    }

    @Override
    public Receive<EvidenceDiscovery> createReceive() {
        ReceiveBuilder<EvidenceDiscovery> builder = newReceiveBuilder();
        builder.onMessage(EvidenceDiscoveryFailed.class, this::onEvidenceDiscoveryFailed);
        builder.onMessage(EvidenceToDiscover.class, this::onEvidenceToDiscover);
        return builder.build();
    }

    private Behavior<EvidenceDiscovery> onEvidenceDiscoveryFailed(EvidenceDiscoveryFailed edf) {
        edf.sender.tell(edf);
        return Behaviors.same();
    }

    private Behavior<EvidenceDiscovery> onEvidenceToDiscover(EvidenceToDiscover etd) {
        ActorRef<EvidenceToDiscover> ref = findSuitableDiscoverer(etd);
        if (ref != null) {
            ref.tell(etd);
        } else {
            etd.sender.tell(new NoSuitableDiscoverer(etd));
        }
        return Behaviors.same();
    }

    private ActorRef<EvidenceToDiscover> findSuitableDiscoverer(EvidenceToDiscover etd) {
        for (Discoverer d : this.discoverers) {
            if (d.accepts(etd.evidenceURI)) {
                return getActorRef(d, etd);
            }
        }
        return null;
    }

    private ActorRef<EvidenceToDiscover> getActorRef(Discoverer d, EvidenceToDiscover etd) {
        Behavior<EvidenceToDiscover> behavior = superviseDiscovererBehavior(d);
        ActorRef<EvidenceToDiscover> ref = getContext().spawn(behavior, d.name());
        getContext().watchWith(ref, new EvidenceDiscoveryFailed(etd));
        return ref;
    }

    private Behavior<EvidenceToDiscover> superviseDiscovererBehavior(Discoverer d) {
        Behavior<EvidenceToDiscover> behavior = Behaviors.supervise(d.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

}
