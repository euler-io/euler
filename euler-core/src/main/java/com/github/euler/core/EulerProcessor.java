package com.github.euler.core;

import java.util.HashMap;
import java.util.Map;

import com.github.euler.message.EvidenceItemToProcess;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerProcessor extends AbstractBehavior<EvidenceItemToProcess> {

    public static Behavior<EvidenceItemToProcess> create(Task... tasks) {
        return Behaviors.setup(ctx -> new EulerProcessor(ctx, tasks));
    }

    private Task[] tasks;
    private Map<String, ActorRef<EvidenceItemToProcess>> mapping;

    public EulerProcessor(ActorContext<EvidenceItemToProcess> ctx, Task... tasks) {
        super(ctx);
        this.tasks = tasks;
        this.mapping = new HashMap<>();
    }

    @Override
    public Receive<EvidenceItemToProcess> createReceive() {
        ReceiveBuilder<EvidenceItemToProcess> builder = newReceiveBuilder();
        builder.onMessage(EvidenceItemToProcess.class, this::onEvidenceItemToProcess);
        return builder.build();
    }

    public Behavior<EvidenceItemToProcess> onEvidenceItemToProcess(EvidenceItemToProcess msg) {
        distributeToTasks(msg);
        return Behaviors.same();
    }

    private void distributeToTasks(EvidenceItemToProcess msg) {
        for (Task task : tasks) {
            if (task.accept(msg)) {
                ActorRef<EvidenceItemToProcess> taskRef = getTaskRef(task);
                taskRef.tell(msg);
            }
        }
    }

    private ActorRef<EvidenceItemToProcess> getTaskRef(Task task) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            return getContext().spawn(task.behavior(), key);
        });
    }

}
