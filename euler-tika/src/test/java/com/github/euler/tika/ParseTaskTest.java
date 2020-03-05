package com.github.euler.tika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

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
        Task task = ParseTask.builder("task")
                .setStreamFactory(new FileStreamFactory())
                .setParsedContentStrategy(new FileStorageStrategy(root, ".txt"))
                .build();
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertNotNull(response.ctx.context(ParseTask.PARSED_CONTENT_FILE));
        assertEquals(content, IOUtils.toString((URI) response.ctx.context(ParseTask.PARSED_CONTENT_FILE), "utf-8").trim());
    }

    @Test
    public void testParseMetadata() throws Exception {
        String content = "some content";
        File file = createFile(content);

        File root = Files.createTempDirectory("dir").toFile();
        Task task = ParseTask.builder("task")
                .setStreamFactory(new FileStreamFactory())
                .setParsedContentStrategy(new FileStorageStrategy(root, ".txt"))
                .build();
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        String contentType = response.ctx.metadata("Content-Type").toString();
        assertTrue(contentType.startsWith("text/plain"));
    }

    private File createFile(String content) throws IOException {
        File file = Files.createTempFile("parse-", ".txt").toFile();
        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.write(content, output, "utf-8");
        }
        return file;
    }

}
