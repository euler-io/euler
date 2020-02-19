package com.github.euler.core;

import akka.actor.typed.Behavior;

public interface Task {

    String name();

    Behavior<TaskCommand> behavior();

    boolean accept(JobTaskToProcess msg);

}
