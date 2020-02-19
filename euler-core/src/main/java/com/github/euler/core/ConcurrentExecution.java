package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class ConcurrentExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(Task[] tasks) {
        return Behaviors.setup(ctx -> new ConcurrentExecution(ctx, tasks));
    }

    private final Task[] tasks;

    private Map<String, ActorRef<TaskCommand>> mapping;

    private ConcurrentExecution(ActorContext<TaskCommand> context, Task[] tasks) {
        super(context);
        this.tasks = tasks;
        this.mapping = new HashMap<>();
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(InternalJobTaskFailed.class, this::onInternalJobTaskFailed);
        return builder.build();
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
        for (Task task : this.tasks) {
            if (task.accept(msg)) {
                ActorRef<TaskCommand> ref = getTaskRef(task, msg);
                ref.tell(msg);
            }
        }
        return Behaviors.same();
    }

    private ActorRef<TaskCommand> getTaskRef(Task task, JobTaskToProcess msg) {
        ActorRef<TaskCommand> ref = mapping.computeIfAbsent(task.name(), (k) -> getContext().spawn(superviseTaskBehavior(task), k));
        getContext().watchWith(ref, new InternalJobTaskFailed(msg));
        return ref;
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    private Behavior<TaskCommand> onInternalJobTaskFailed(InternalJobTaskFailed msg) {
        msg.replyTo.tell(new JobTaskFailed(msg.uri, msg.itemURI));
        return Behaviors.same();
    }

    private static class InternalJobTaskFailed implements TaskCommand {

        public final URI uri;
        public final URI itemURI;
        public final ActorRef<ProcessorCommand> replyTo;

        public InternalJobTaskFailed(JobTaskToProcess msg) {
            this.uri = msg.uri;
            this.itemURI = msg.itemURI;
            this.replyTo = msg.replyTo;
        }

    }

}
