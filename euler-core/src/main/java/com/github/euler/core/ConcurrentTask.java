package com.github.euler.core;

import java.util.Arrays;

import akka.actor.typed.Behavior;

public class ConcurrentTask implements Task {

    private final String name;
    private final Task[] tasks;

    public ConcurrentTask(String name, Task... tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return ConcurrentExecution.create(this.tasks);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return Arrays.stream(this.tasks).anyMatch(t -> t.accept(msg));
    }

}
