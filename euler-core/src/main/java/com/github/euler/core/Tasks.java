package com.github.euler.core;

import java.util.function.Predicate;
import java.util.function.Supplier;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public final class Tasks {

    private Tasks() {
        super();
    }

    public static Task concurrent(String name, Task... tasks) {
        return new ConcurrentTask(name, tasks);
    }

    public static Task pool(String name, int size, Task task) {
        return new PooledTask(name, size, task);
    }

    public static Task pipeline(String name, Task... tasks) {
        return new PipelineTask(name, tasks);
    }

    public static Task setup(final String name, final Predicate<JobTaskToProcess> accept, final Supplier<Behavior<TaskCommand>> behavior) {
        return new Task() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean accept(JobTaskToProcess msg) {
                return accept.test(msg);
            }

            @Override
            public Behavior<TaskCommand> behavior() {
                return behavior.get();
            }

        };
    }

    public static Task accept(String name, final Supplier<Behavior<TaskCommand>> behavior) {
        return setup(name, (msg) -> true, behavior);
    }

    public static Task accept(String name) {
        return accept(name, () -> voidBehavior());
    }

    public static Task notAccept(String name, final Supplier<Behavior<TaskCommand>> behavior) {
        return setup(name, (msg) -> false, behavior);
    }

    public static Task notAccept(String name) {
        return notAccept(name, () -> voidBehavior());
    }

    public static Task foward(String name, ActorRef<TaskCommand> ref) {
        return setup(name, (msg) -> true, () -> fowardBehavior(ref));
    }

    public static Task empty(String name) {
        return accept(name, () -> emptyBehavior());
    }

    public static Task empty(String name, ProcessingContext ctx) {
        return accept(name, () -> emptyBehavior(ctx));
    }

    public static Behavior<TaskCommand> voidBehavior() {
        return Behaviors.receive(TaskCommand.class)
                .onMessage(TaskCommand.class, (msg) -> Behaviors.same())
                .build();
    }

    public static Behavior<TaskCommand> fowardBehavior(ActorRef<TaskCommand> ref) {
        return Behaviors.receive(TaskCommand.class)
                .onAnyMessage((msg) -> {
                    ref.tell(msg);
                    return Behaviors.same();
                })
                .build();
    }

    public static Behavior<TaskCommand> emptyBehavior() {
        return emptyBehavior(ProcessingContext.EMPTY);
    }

    public static Behavior<TaskCommand> emptyBehavior(ProcessingContext ctx) {
        return Behaviors.receive(TaskCommand.class)
                .onMessage(JobTaskToProcess.class, (msg) -> {
                    msg.replyTo.tell(new JobTaskFinished(msg, ctx));
                    return Behaviors.same();
                })
                .build();
    }
}
