package com.github.euler.core;

import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.TaskCommand;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class FowardingTask implements Task {

    private ActorRef<TaskCommand> ref;

    public FowardingTask(ActorRef<TaskCommand> ref) {
        this.ref = ref;
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return Behaviors.receive(TaskCommand.class).onAnyMessage((msg) -> {
            ref.tell(msg);
            return Behaviors.same();
        }).build();
    }

    @Override
    public boolean accept(JobItemToProcess msg) {
        return true;
    }

}
