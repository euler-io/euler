package com.github.euler.core;

import java.time.Duration;
import java.util.Arrays;

import akka.actor.typed.Behavior;

public class PipelineTask implements Task {

    private final String name;
    private final Task[] tasks;

    public PipelineTask(String name, Task... tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return PipelineExecution.create(tasks);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return Arrays.stream(this.tasks).anyMatch(t -> t.accept(msg));
    }

    public Task[] getTasks() {
        return tasks;
    }

    @Override
    public boolean isFlushable() {
        return Arrays.stream(this.tasks).anyMatch(t -> t.isFlushable());
    }

    @Override
    public Duration getTimeout() {
        return Arrays.stream(this.tasks)
                .map(t -> t.getTimeout())
                .reduce(Duration.ZERO, (d1, d2) -> d1.plus(d2));
    }

}
