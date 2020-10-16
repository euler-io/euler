package com.github.euler.core;

import java.time.Duration;

import akka.actor.typed.Behavior;

public class PooledTask implements Task {

    private final String name;
    private final int size;
    private Task task;

    public PooledTask(String name, int size, Task task) {
        this.name = name;
        this.size = size;
        this.task = task;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return PooledExecution.create(size, task);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return task.accept(msg);
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean isFlushable() {
        return task.isFlushable();
    }

    @Override
    public Duration getTimeout() {
        return task.getTimeout();
    }

}
