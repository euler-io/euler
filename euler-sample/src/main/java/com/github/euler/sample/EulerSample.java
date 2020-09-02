package com.github.euler.sample;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpHost;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Euler;
import com.github.euler.core.JobProcessed;
import com.github.euler.core.SourceExecution;
import com.github.euler.elasticsearch.ElasticSearchTask;
import com.github.euler.file.BasicFilePropertiesTask;
import com.github.euler.file.FileSource;
import com.github.euler.file.FileStorageStrategy;
import com.github.euler.file.FileStreamFactory;
import com.github.euler.tika.MimeTypeDetectTask;
import com.github.euler.tika.ParseTask;

/**
 * A simple euler file processing pipeline.
 * This samples assumes a non-authenticated elasticsearch instance running on localhost: 9200.
 *
 */
public class EulerSample {

    public static void main(String[] args) throws Exception {
        StreamFactory sf = new FileStreamFactory();
        Detector detector = TikaConfig.getDefaultConfig().getDetector();

        File tmp = Files.createTempDirectory("tmp").toFile();
        StorageStrategy parsedContentStrategy = new FileStorageStrategy(tmp, ".txt");
        StorageStrategy embeddedContentStrategy = new FileStorageStrategy(tmp, ".tmp");
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200));
        RestHighLevelClient client = new RestHighLevelClient(builder);

        Euler euler = Euler.builder()
                .source(SourceExecution.create(new FileSource()))
                .task(new BasicFilePropertiesTask("basic-file-properties"))
                .task(new MimeTypeDetectTask("mime-type-detect", sf, detector))
                .task(ParseTask.builder("parse", sf, parsedContentStrategy, embeddedContentStrategy).build())
                .task(ElasticSearchTask.builder("elasticsearch-sink", sf, client).setIndex("euler-files").build())
                .build();

        URI uri = EulerSample.class.getClassLoader().getResource("File.txt").toURI();
        CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(10));
        JobProcessed jobProcessed = future.get();
        System.out.println("Finished processing " + jobProcessed.uri);
    }

}
