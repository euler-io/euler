package com.github.euler.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class PooledExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(int size, Task task) {
        return Behaviors.setup((ctx) -> new PooledExecution(ctx, size, task));
    }

    private List<ActorRef<TaskCommand>> pool;
    private int position;
    private int size;
    private Task task;

    public PooledExecution(ActorContext<TaskCommand> context, int size, Task task) {
        super(context);
        pool = new ArrayList<ActorRef<TaskCommand>>(size);
        this.size = size;
        this.task = task;
        position = 0;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(Flush.class, this::onFlush);
        return builder.build();
    }

    public Behavior<TaskCommand> onFlush(Flush msg) {
        for (ActorRef<TaskCommand> taskRef : this.pool) {
            taskRef.tell(msg);
        }
        return Behaviors.same();
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(MiddleManagement.create(t)).onFailure(SupervisorStrategy.restart());
        return behavior;
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
        ActorRef<TaskCommand> taskRef = getNextTaskRef();
        taskRef.tell(msg);
        return Behaviors.same();
    }

    private ActorRef<TaskCommand> getNextTaskRef() {
        if (pool.size() - 1 < position) {
            pool.add(getContext().spawn(superviseTaskBehavior(task), task.name() + "-" + position));
        }
        ActorRef<TaskCommand> taskRef = pool.get(position);
        if (position + 1 == size) {
            position = 0;
        } else {
            position++;
        }
        return taskRef;
    }

    private static class MiddleManagement extends AbstractBehavior<TaskCommand> {

        public static Behavior<TaskCommand> create(Task task) {
            return Behaviors.setup((ctx) -> new MiddleManagement(ctx, task));
        }

        private final Task task;
        private ActorRef<TaskCommand> taskRef;

        public MiddleManagement(ActorContext<TaskCommand> context, Task task) {
            super(context);
            this.task = task;
        }

        @Override
        public Receive<TaskCommand> createReceive() {
            ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
            builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
            builder.onMessage(InternalJobTaskFailed.class, this::onInternalJobTaskFailed);
            builder.onMessage(Flush.class, this::onFlush);
            builder.onAnyMessage((m) -> {
                System.out.println(m);
                return Behaviors.same();
            });
            return builder.build();
        }

        public Behavior<TaskCommand> onFlush(Flush msg) {
            if (taskRef != null) {
                taskRef.tell(msg);
            }
            return Behaviors.same();
        }

        private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
            ActorRef<TaskCommand> ref = getTaskRef(msg);
            ref.tell(msg);
            return Behaviors.same();
        }

        private ActorRef<TaskCommand> getTaskRef(JobTaskToProcess msg) {
            if (taskRef == null) {
                taskRef = getContext().spawn(superviseTaskBehavior(this.task), "middle-management");
                getContext().watchWith(taskRef, new InternalJobTaskFailed(msg));
            }
            return taskRef;
        }

        private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
            Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.stop());
            return behavior;
        }

        private Behavior<TaskCommand> onInternalJobTaskFailed(InternalJobTaskFailed msg) {
            taskRef = null;
            msg.replyTo.tell(new JobTaskFailed(msg.uri, msg.itemURI));
            return Behaviors.same();
        }

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
