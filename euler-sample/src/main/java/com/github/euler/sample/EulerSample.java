package com.github.euler.sample;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpHost;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Euler;
import com.github.euler.core.JobProcessed;
import com.github.euler.core.SourceExecution;
import com.github.euler.core.Tasks;
import com.github.euler.elasticsearch.ElasticsearchContentTask;
import com.github.euler.file.BasicFilePropertiesTask;
import com.github.euler.file.FileSource;
import com.github.euler.file.FileStorageStrategy;
import com.github.euler.file.FileStreamFactory;
import com.github.euler.tika.MimeTypeDetectTask;
import com.github.euler.tika.ParseTask;

/**
 * A simple euler file processing pipeline. This samples assumes a
 * non-authenticated elasticsearch instance running on localhost:9200.
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

        String indexName = "euler-files";
        createIndex(client, indexName);

        Euler euler = null;
        try {
            euler = Euler.builder()
                    .source(SourceExecution.create(new FileSource()))
                    .task(Tasks.pipeline("main-pipeline",
                            new BasicFilePropertiesTask("basic-file-properties"),
                            new MimeTypeDetectTask("mime-type-detect", sf, detector),
                            ParseTask.builder("parse", sf, parsedContentStrategy, embeddedContentStrategy).build(),
                            ElasticsearchContentTask.builder("elasticsearch-content-sink", sf, client).setIndex(indexName).build()))
                    .build();

            URI uri = EulerSample.class.getClassLoader().getResource("File.txt").toURI();
            CompletableFuture<JobProcessed> future = euler.process(uri, Duration.ofSeconds(10));
            JobProcessed jobProcessed = future.get();
            System.out.println("Finished processing " + jobProcessed.uri);
        } finally {
            if (euler != null) {
                euler.close();
            }
            client.close();
        }
    }

    private static void createIndex(RestHighLevelClient client, String indexName) throws IOException {
        String source = "{ \"properties\": {\"join_field\": { \"type\": \"join\", \"relations\": { \"item\": \"fragment\" } } } }";
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName)
                .mapping(source, XContentType.JSON);
        client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

}
