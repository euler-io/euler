package com.github.euler.sample;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.euler.core.Euler;
import com.github.euler.core.JobProcessed;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.SourceExecution;
import com.github.euler.core.Tasks;

import akka.actor.typed.Behavior;

public class FunctionalStyleSampleSource {

    public static void main(String[] args) throws Exception {
        Behavior<SourceCommand> source = SourceExecution.create((uri, listener) -> {
            try {

                URI itemURI = new URI("file:///newly/found/item");
                listener.notifyItemFound(uri, itemURI, ProcessingContext.EMPTY);

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        URI uri = new URI("file:///some/resource");

        Euler euler = Euler.builder()
                .source(source)
                .task(Tasks.empty("no-op-task"))
                .build();

        CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(5));
        JobProcessed jobProcessed = future.get();
        System.out.println("Finished processing " + jobProcessed.uri);

    }

}
