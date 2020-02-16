package com.github.euler.core;

import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.TaskCommand;

import akka.actor.typed.Behavior;

public interface Task {

    String name();

    Behavior<TaskCommand> behavior();

    boolean accept(JobItemToProcess msg);

}
