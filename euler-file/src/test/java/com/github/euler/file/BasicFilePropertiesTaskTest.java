package com.github.euler.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import org.junit.Test;

import com.github.euler.common.CommonMetadata;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class BasicFilePropertiesTaskTest extends AkkaTest {

    @Test
    public void testAcceptOnlyFileProtocol() throws Exception {
        BasicFilePropertiesTask task = new BasicFilePropertiesTask("task");
        assertFalse(task.accept(new JobTaskToProcess(new URI("not-file:///some/path"), new URI("not-file:///some/path/item"), null, null)));
        assertTrue(task.accept(new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), null, null)));
    }

    @Test
    public void testWhenJobTaskExtractBasicFileProperties() throws Exception {
        File root = Files.createTempDirectory("test").toFile();
        File dir = new File(root, "dir");
        dir.mkdirs();
        File file = new File(dir, "item.txt");
        file.createNewFile();

        URI uri = dir.toURI();
        URI itemURI = file.toURI();

        Task task = new BasicFilePropertiesTask("task");

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(task.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(uri, itemURI, ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        ProcessingContext ctx = response.ctx;

        assertEquals("item.txt", ctx.metadata(CommonMetadata.NAME));
        assertEquals(Long.valueOf(0l), (Long) ctx.metadata(CommonMetadata.SIZE));
        assertFalse((Boolean) ctx.metadata(CommonMetadata.IS_DIRECTORY));
        assertEquals("dir/item.txt", ctx.metadata(CommonMetadata.PATH));
        assertNotNull(ctx.metadata(CommonMetadata.CREATED_DATETIME));
        assertNotNull(ctx.metadata(CommonMetadata.LAST_MODIFIED_DATETIME));
    }

}
