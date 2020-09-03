package com.github.euler.core;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

class Flushable implements Task {

    private boolean flushable;
    protected ActorRef<TaskCommand> ref;

    protected Flushable(boolean flushable, ActorRef<TaskCommand> ref) {
        super();
        this.flushable = flushable;
        this.ref = ref;
    }

    @Override
    public String name() {
        return "flushable";
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return Behaviors.receive(TaskCommand.class)
                .onAnyMessage((msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return true;
    }

    @Override
    public boolean isFlushable() {
        return flushable;
    }

}