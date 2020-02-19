package com.github.euler.core;

import akka.actor.typed.Behavior;

public class SupervisedTask implements Task {

    private final String name;
    private final int routees;
    private Task task;

    public SupervisedTask(String name, int routees, Task task) {
        this.name = name;
        this.routees = routees;
        this.task = task;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return null;
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return task.accept(msg);
    }

}
