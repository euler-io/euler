package com.github.euler.core;

import java.time.Duration;

import akka.actor.typed.Behavior;

public interface Task {

    String name();

    Behavior<TaskCommand> behavior();

    boolean accept(JobTaskToProcess msg);

    default boolean isFlushable() {
        return false;
    }

    default Duration getTimeout() {
        return Duration.ZERO;
    }

}
