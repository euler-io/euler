package com.github.euler.sample;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.euler.core.AbstractTask;
import com.github.euler.core.Euler;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.JobProcessed;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.Sources;
import com.github.euler.core.Task;

public class FunctionalStyleSampleTask {

    public static void main(String[] args) throws Exception {
        Task functionalTask = new AbstractTask("functional-task") {

            @Override
            protected ItemProcessor itemProcessor() {
                return (item) -> {
                    return ProcessingContext.builder()
                            .metadata("functional-sample", "metadata for " + item.itemURI)
                            .context("functional-sample", "context for " + item.itemURI)
                            .build();
                };
            }

        };

        URI uri = new URI("file:///some/resource");

        Euler euler = Euler.builder()
                .source(Sources.fixedItemBehavior(uri))
                .task(functionalTask)
                .build();

        CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(5));
        JobProcessed jobProcessed = future.get();
        System.out.println("Finished processing " + jobProcessed.uri);
    }
}
