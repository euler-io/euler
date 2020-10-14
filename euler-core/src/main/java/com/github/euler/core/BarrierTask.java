package com.github.euler.core;

import akka.actor.typed.Behavior;

public class BarrierTask implements Task {

    private final String name;
    private final Task task;
    private final BarrierCondition condition;

    public BarrierTask(String name, Task task, BarrierCondition condition) {
        super();
        this.name = name;
        this.task = task;
        this.condition = condition;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return BarrierExecution.create(task, condition);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return task.accept(msg);
    }

    @Override
    public boolean isFlushable() {
        return task.isFlushable();
    }

}
