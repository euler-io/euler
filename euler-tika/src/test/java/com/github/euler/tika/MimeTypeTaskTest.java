package com.github.euler.tika;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.junit.Test;

import com.github.euler.common.CommonMetadata;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;
import com.github.euler.file.FileStreamFactory;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class MimeTypeTaskTest extends AkkaTest {

    @Test
    public void testDetectTextMime() throws Exception {
        String content = "some content";
        File file = createFile(content);

        Task task = new MimeTypeDetectTask("mime-type-task", new FileStreamFactory(), TikaConfig.getDefaultConfig().getDetector());
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals("text/plain", response.ctx.metadata(CommonMetadata.MIME_TYPE));
    }

    @Test
    public void testDetectZipMime() throws Exception {
        File file = Paths.get(ParseTaskTest.class.getClassLoader().getResource("content.zip").toURI()).normalize().toFile();

        Task task = new MimeTypeDetectTask("mime-type-task", new FileStreamFactory(), TikaConfig.getDefaultConfig().getDetector());
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals("application/zip", response.ctx.metadata(CommonMetadata.MIME_TYPE));
    }

    @Test
    public void testDetectDirectory() throws Exception {
        File file = Files.createTempDirectory("dir").toFile();

        Task task = new MimeTypeDetectTask("mime-type-task", new FileStreamFactory(), TikaConfig.getDefaultConfig().getDetector());
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        ProcessingContext ctx = ProcessingContext.builder()
                .metadata(CommonMetadata.IS_DIRECTORY, true)
                .build();
        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ctx, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals("text/directory", response.ctx.metadata(CommonMetadata.MIME_TYPE));
    }

    private File createFile(String content) throws IOException {
        File file = Files.createTempFile("parse-", ".txt").toFile();
        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.write(content, output, "utf-8");
        }
        return file;
    }

}
