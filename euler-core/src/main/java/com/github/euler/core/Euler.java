package com.github.euler.core;

import java.net.URI;
import java.time.Duration;
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

    public synchronized CompletableFuture<JobProcessed> process(URI uri, Duration duration) {
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

    @Override
    public void close() throws Exception {
        if (system != null) {
            system.terminate();
        }
    }

}
