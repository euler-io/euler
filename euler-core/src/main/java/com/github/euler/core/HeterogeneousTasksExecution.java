package com.github.euler.core;

import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;

public abstract class HeterogeneousTasksExecution extends TasksExecution {

    private Map<String, ActorRef<TaskCommand>> mapping;

    public HeterogeneousTasksExecution(ActorContext<TaskCommand> context) {
        super(context);
        this.mapping = new HashMap<>();
    }

    protected ActorRef<TaskCommand> getTaskRef(Task task) {
        return this.mapping.get(task.name());
    }

    protected boolean isTaskActive(Task task) {
        return this.mapping.containsKey(task.name());
    }

    protected ActorRef<TaskCommand> getOrSpawnTaskRef(Task task) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            Behavior<TaskCommand> behavior = superviseTaskBehavior(task);
            ActorRef<TaskCommand> ref = getContext().spawn(behavior, key);
            return ref;
        });
    }

}
