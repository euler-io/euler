package com.github.euler.core;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AskPattern;

public class Euler implements AutoCloseable {

    private ActorSystem<JobCommand> system;

    private final Behavior<SourceCommand> sourceBehavior;
    private final Task[] tasks;

    public Euler(Behavior<SourceCommand> sourceBehavior, Task... tasks) {
        this.sourceBehavior = sourceBehavior;
        this.tasks = tasks;
    }

    public CompletableFuture<JobProcessed> process(URI uri, Duration duration) {
        return process(uri, ProcessingContext.EMPTY, duration);
    }

    public synchronized CompletableFuture<JobProcessed> process(URI uri, ProcessingContext ctx, Duration duration) {
        if (system != null) {
            throw new IllegalStateException("System already started");
        }
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks);
        system = ActorSystem.create(JobExecution.create(sourceBehavior, processorBehavior), "euler-" + UUID.randomUUID().toString());

        CompletionStage<JobCommand> result = AskPattern.ask(system, (replyTo) -> new Job(uri, replyTo), duration, system.scheduler());
        CompletionStage<JobProcessed> completionStage = result.thenCompose((r) -> {
            if (r instanceof JobProcessed) {
                return CompletableFuture.completedFuture((JobProcessed) r);
            } else {
                throw new IllegalStateException("Unexpected reply: " + r.getClass());
            }
        });
        return completionStage.toCompletableFuture();
    }

    public Behavior<SourceCommand> getSourceBehavior() {
        return sourceBehavior;
    }

    public Task[] getTasks() {
        return tasks;
    }

    @Override
    public void close() throws Exception {
        if (system != null) {
            system.terminate();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Behavior<SourceCommand> sourceBehavior;
        private List<Task> tasks;

        private Builder() {
            sourceBehavior = null;
            tasks = new ArrayList<Task>();
        }

        public Builder task(Task... task) {
            tasks.addAll(Arrays.asList(task));
            return this;
        }

        public Builder source(Behavior<SourceCommand> source) {
            this.sourceBehavior = source;
            return this;
        }

        public Euler build() {
            Objects.requireNonNull(sourceBehavior, "sourceBehavior cannot be null.");
            if (this.tasks.isEmpty()) {
                throw new IllegalArgumentException("At least one task must be provided.");
            }
            return new Euler(sourceBehavior, tasks.stream().toArray(Task[]::new));
        }

    }

}
