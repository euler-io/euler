package com.github.euler.core;

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

    private final List<ActorRef<TaskCommand>> pool;
    private final int size;
    private final Task task;

    private int position;

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

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.restart());
        return behavior;
    }

}
