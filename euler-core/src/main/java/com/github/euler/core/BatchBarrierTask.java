package com.github.euler.core;

import akka.actor.typed.Behavior;

public class BatchBarrierTask implements Task {

    private final String name;
    private final int batchMaxSize;
    private final Task task;
    private final BatchBarrierCondition condition;

    public BatchBarrierTask(String name, int batchMaxSize, Task task, BatchBarrierCondition condition) {
        super();
        this.name = name;
        this.batchMaxSize = batchMaxSize;
        this.task = task;
        this.condition = condition;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return BatchBarrierExecution.create(task, batchMaxSize, condition);
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
