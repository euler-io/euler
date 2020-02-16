package com.github.euler.core;

import java.net.URI;
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
import akka.actor.typed.SupervisorStrategy;
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
        builder.onMessage(JobTaskFailed.class, this::onJobTaskFailed);
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
                ActorRef<TaskCommand> taskRef = getTaskRef(task, msg);
                taskRef.tell(new JobTaskToProcess(msg, getContext().getSelf()));
            }
        }
    }

    private ActorRef<TaskCommand> getTaskRef(Task task, JobItemToProcess msg) {
        return mapping.computeIfAbsent(task.name(), (key) -> {
            Behavior<TaskCommand> behavior = superviseTaskBehavior(task);
            ActorRef<TaskCommand> ref = getContext().spawn(behavior, key);
            getContext().watchWith(ref, new JobTaskFailed(msg, task.name()));
            return ref;
        });
    }

    private Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.stop());
        return behavior;
    }

    public Behavior<ProcessorCommand> onJobTaskFailed(JobTaskFailed msg) {
        state.onJobTaskFailed(msg);
        mapping.remove(msg.taskName);
        if (state.isProcessed(msg)) {
            ActorRef<EulerCommand> replyTo = state.getReplyTo(msg);
            replyTo.tell(new JobItemProcessed(msg.uri, msg.itemURI));
        }
        return Behaviors.same();
    }

    static class JobTaskFailed implements ProcessorCommand {

        public final URI uri;
        public final URI itemURI;
        public final ActorRef<EulerCommand> replyTo;
        public final String taskName;

        public JobTaskFailed(JobItemToProcess msg, String taskName) {
            this.uri = msg.uri;
            this.itemURI = msg.itemURI;
            this.replyTo = msg.replyTo;
            this.taskName = taskName;
        }

    }
}
