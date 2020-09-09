package com.github.euler.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean isFlushable() {
        return Arrays.stream(this.tasks).anyMatch(t -> t.isFlushable());
    }

    public Task[] getTasks() {
        return tasks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private List<Task> tasks;

        private Builder() {
            this.name = null;
            this.tasks = new ArrayList<Task>();
        }

        public Builder task(Task... task) {
            this.tasks.addAll(Arrays.asList(task));
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ConcurrentTask build() {
            Objects.requireNonNull(name, "name cannot be null.");
            if (this.tasks.isEmpty()) {
                throw new IllegalArgumentException("At least one task must be provided.");
            }
            return new ConcurrentTask(this.name, this.tasks.stream().toArray(Task[]::new));
        }
    }

}
