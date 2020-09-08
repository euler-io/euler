package com.github.euler.core;

import java.net.URI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public abstract class AbstractTask implements Task {

    private final String name;

    public AbstractTask(String name) {
        super();
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return Behaviors.setup((context) -> new ItemProcessorExecution(context, itemProcessor()));
    }

    protected abstract ItemProcessor itemProcessor();

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return accept(msg.uri, msg.itemURI, msg.ctx);
    }

    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        return true;
    }

}
