package com.github.euler.core;

import com.github.euler.message.EvidenceItemToProcess;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerProcessor extends AbstractBehavior<EvidenceItemToProcess> {

    public static Behavior<EvidenceItemToProcess> create(Task task) {
        return Behaviors.setup(ctx -> new EulerProcessor(ctx, task));
    }

    private Task task;

    public EulerProcessor(ActorContext<EvidenceItemToProcess> ctx, Task task) {
        super(ctx);
        this.task = task;
    }

    @Override
    public Receive<EvidenceItemToProcess> createReceive() {
        ReceiveBuilder<EvidenceItemToProcess> builder = newReceiveBuilder();
        builder.onMessage(EvidenceItemToProcess.class, this::onEvidenceItemToProcess);
        return builder.build();
    }

    public Behavior<EvidenceItemToProcess> onEvidenceItemToProcess(EvidenceItemToProcess msg) {
        ActorRef<EvidenceItemToProcess> taskRef = getTaskRef();
        taskRef.tell(msg);
        return Behaviors.same();
    }

    private ActorRef<EvidenceItemToProcess> getTaskRef() {
        return getContext().spawn(task.behavior(), task.name());
    }

}
