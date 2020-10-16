package com.github.euler.core;

import java.time.Duration;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;

public abstract class TaskBehavior extends AbstractBehavior<TaskCommand> {

    public TaskBehavior(ActorContext<TaskCommand> context) {
        super(context);
    }

    public abstract String name();

    public abstract boolean accept(JobTaskToProcess msg);

    public boolean isFlushable() {
        return false;
    }

    public Duration getTimeout() {
        return Duration.ZERO;
    }

}
