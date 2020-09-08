package com.github.euler.sample;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.euler.configuration.EulerConfigConverter;
import com.github.euler.core.Euler;
import com.github.euler.core.JobProcessed;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * A simple euler file processing pipeline loaded from a config file. This
 * samples assumes a non-authenticated elasticsearch instance running on
 * localhost:9200.
 *
 */
public class LoadConfigSample {

    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.parseResources("sample-euler.conf");

        EulerConfigConverter converter = new EulerConfigConverter();
        try (Euler euler = converter.createEuler(config)) {
            URI uri = new URI("file:///some/path");
            CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(10));
            JobProcessed jobProcessed = future.get();
            System.out.println("Finished processing " + jobProcessed.uri);
        }
    }

}
