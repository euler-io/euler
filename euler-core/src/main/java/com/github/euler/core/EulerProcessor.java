package com.github.euler.core;

import java.util.HashMap;
import java.util.Map;

import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobItemProcessed;
import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.JobTaskFinished;
import com.github.euler.command.JobTaskToProcess;
import com.github.euler.command.ProcessorCommand;
import com.github.euler.command.TaskCommand;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class EulerProcessor extends AbstractBehavior<ProcessorCommand> {

    public static Behavior<ProcessorCommand> create(Task... tasks) {
        return Behaviors.setup(ctx -> new EulerProcessor(ctx, tasks));
    }

    private Task[] tasks;
    private Map<String, ActorRef<TaskCommand>> mapping;

    private EulerProcessorState state;

    public EulerProcessor(ActorContext<ProcessorCommand> ctx, Task... tasks) {
        super(ctx);
        this.tasks = tasks;
        this.mapping = new HashMap<>();
        this.state = new EulerProcessorState();
    }

    @Override
    public Receive<ProcessorCommand> createReceive() {
        ReceiveBuilder<ProcessorCommand> builder = newReceiveBuilder();
        builder.onMessage(JobItemToProcess.class, this::onJobItemToProcess);
        builder.onMessage(JobTaskFinished.class, this::onJobTaskFinished);
        return builder.build();
    }

    public Behavior<ProcessorCommand> onJobItemToProcess(JobItemToProcess msg) {
        state.onJobItemToProcess(msg);
        distributeToTasks(msg);
        return Behaviors.same();
    }

    public Behavior<ProcessorCommand> onJobTaskFinished(JobTaskFinished msg) {
        state.onJobTaskFinished(msg);
        if (state.isProcessed(msg)) {
            ActorRef<EulerCommand> replyTo = state.getReplyTo(msg);
            replyTo.tell(new JobItemProcessed(msg.uri, msg.itemURI));
        }
        return Behaviors.same();
    }

    private void distributeToTasks(JobItemToProcess msg) {
        for (Task task : tasks) {
            if (task.accept(msg)) {
                ActorRef<TaskCommand> taskRef = getTaskRef(task);
                taskRef.tell(new JobTaskToProcess(msg, getContext().getSelf()));
            }
        }
    }

    private ActorRef<TaskCommand> getTaskRef(Task task) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            return getContext().spawn(task.behavior(), key);
        });
    }

}
