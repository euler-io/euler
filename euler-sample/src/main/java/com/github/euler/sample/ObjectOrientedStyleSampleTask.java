package com.github.euler.sample;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.euler.core.AbstractTask;
import com.github.euler.core.Euler;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.JobProcessed;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.Sources;

public class ObjectOrientedStyleSampleTask {

    public static class OOTask extends AbstractTask {

        public OOTask(String name) {
            super(name);
        }

        @Override
        protected ItemProcessor itemProcessor() {
            return new OOSampleProcessor();
        }

    }

    public static class OOSampleProcessor implements ItemProcessor {

        @Override
        public ProcessingContext process(Item item) {
            ProcessingContext.Builder builder = ProcessingContext.builder();
            builder.metadata("oo-sample", "metadata for " + item.itemURI);
            builder.context("oo-sample", "context for " + item.itemURI);
            return builder.build();
        }

    }

    public static void main(String[] args) throws Exception {

        URI uri = new URI("file:///some/resource");

        Euler euler = Euler.builder()
                .source(Sources.fixedItemBehavior(uri))
                .task(new OOTask("oo-task"))
                .build();

        CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(5));
        JobProcessed jobProcessed = future.get();
        System.out.println("Finished processing " + jobProcessed.uri);
    }

}
