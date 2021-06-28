package com.github.euler.tika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.core.EmbeddedItemFound;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;
import com.github.euler.file.FileStorageStrategy;
import com.github.euler.file.FileStreamFactory;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class ParseTaskTest extends AkkaTest {

    @Test
    public void testParseFile() throws Exception {
        String content = "some content";
        File file = createFile(content);

        File root = Files.createTempDirectory("dir").toFile();
        Task task = ParseTask
                .builder("task", new FileStreamFactory(), new FileStorageStrategy(root, ".txt"), new FileStorageStrategy(root, ".tmp"))
                .build();
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertNotNull(response.ctx.context(CommonContext.PARSED_CONTENT_FILE));
        assertEquals(content, IOUtils.toString((URI) response.ctx.context(CommonContext.PARSED_CONTENT_FILE), "utf-8").trim());
    }

    @Test
    public void testParseMetadata() throws Exception {
        String content = "some content";
        File file = createFile(content);

        File root = Files.createTempDirectory("dir").toFile();
        Task task = ParseTask
                .builder("task", new FileStreamFactory(), new FileStorageStrategy(root, ".txt"), new FileStorageStrategy(root, ".tmp"))
                .build();
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        String contentType = response.ctx.metadata("Content-Type").toString();
        assertTrue(contentType.startsWith("text/plain"));
    }

    @Test
    public void testParseEmbedded() throws Exception {
        File file = Paths.get(ParseTaskTest.class.getClassLoader().getResource("content.zip").toURI()).normalize().toFile();

        File root = Files.createTempDirectory("dir").toFile();
        File tmp = Files.createTempDirectory("tmp").toFile();
        EmbeddedStrategy embeddedStrategy = DefaultEmbeddedStrategy.builder()
                .setIncludeExtractEmbeddedRegex(".+")
                .setExcludeExtractEmbeddedRegex("a^")
                .build();
        Task task = ParseTask
                .builder("task", new FileStreamFactory(), new FileStorageStrategy(root, ".txt"), new FileStorageStrategy(tmp, ".tmp"))
                .setEmbeddedStrategy(embeddedStrategy)
                .build();
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        ProcessingContext ctx = ProcessingContext.builder().metadata(CommonMetadata.MIME_TYPE, "application/zip").build();
        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ctx, probe.ref());
        ref.tell(msg);

        EmbeddedItemFound response = probe.expectMessageClass(EmbeddedItemFound.class, Duration.ofDays(1));

        assertTrue(response.ctx.metadata().containsKey(CommonMetadata.CREATED_DATETIME));
        assertTrue(response.ctx.metadata().containsKey(CommonMetadata.LAST_MODIFIED_DATETIME));
        assertTrue(response.ctx.metadata().containsKey(CommonMetadata.NAME));

        assertTrue(response.ctx.context().containsKey(CommonContext.TEMPORARY_URI));

        probe.expectMessageClass(JobTaskFinished.class);
    }

    private File createFile(String content) throws IOException {
        File file = Files.createTempFile("parse-", ".txt").toFile();
        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.write(content, output, "utf-8");
        }
        return file;
    }

}
