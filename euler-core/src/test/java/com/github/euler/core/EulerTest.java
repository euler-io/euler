package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import akka.actor.typed.Behavior;

public class EulerTest {

    @Test
    public void testProcessJob() throws Exception {
        URI uri = new URI("file:///some/path");
        Task task = Tasks.empty("task");
        Behavior<SourceCommand> sourceBehavior = Sources.fixedItemBehavior(new URI("file:///some/path/item"));

        try (Euler euler = new Euler(sourceBehavior, task)) {
            CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(1));
            JobProcessed jobProcessed = future.get();
            assertEquals(uri, jobProcessed.uri);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotReuseInstance() throws Exception {
        URI uri = new URI("file:///some/path");
        Task task = Tasks.empty("task");
        Behavior<SourceCommand> sourceBehavior = Sources.fixedItemBehavior(new URI("file:///some/path/item"));

        try (Euler euler = new Euler(sourceBehavior, task)) {
            euler.process(uri, Duration.ofSeconds(1));
            euler.process(uri, Duration.ofSeconds(1));
        }
    }

}
