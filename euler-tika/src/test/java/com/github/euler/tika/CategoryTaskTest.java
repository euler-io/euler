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

public class CategoryTaskTest extends AkkaTest {

    @Test
    public void testDetectTextCategory() throws Exception {
        String content = "some content";
        File file = createFile(content);

        Task task = new CategoryDetectTask("category-task", new FileStreamFactory(), TikaConfig.getDefaultConfig().getDetector());
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals("text/plain", response.ctx.metadata(CommonMetadata.CATEGORY));
    }

    @Test
    public void testDetectZipCategory() throws Exception {
        File file = Paths.get(ParseTaskTest.class.getClassLoader().getResource("content.zip").toURI()).normalize().toFile();

        Task task = new CategoryDetectTask("category-task", new FileStreamFactory(), TikaConfig.getDefaultConfig().getDetector());
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(file.toURI(), file.toURI(), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals("application/zip", response.ctx.metadata(CommonMetadata.CATEGORY));
    }

    private File createFile(String content) throws IOException {
        File file = Files.createTempFile("parse-", ".txt").toFile();
        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.write(content, output, "utf-8");
        }
        return file;
    }

}
