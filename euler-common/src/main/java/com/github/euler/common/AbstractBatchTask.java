package com.github.euler.common;

import java.util.function.Supplier;

import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public abstract class AbstractBatchTask implements Task {

    private final String name;
    private final Supplier<Batch> batchFactory;

    public AbstractBatchTask(String name, Supplier<Batch> batchFactory) {
        super();
        this.name = name;
        this.batchFactory = batchFactory;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return BatchExecution.create(batchFactory.get());
    }

}
