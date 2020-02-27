package com.github.euler.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import com.github.euler.core.EulerCommand;
import com.github.euler.core.JobItemFound;
import com.github.euler.core.JobToScan;
import com.github.euler.core.ScanFinished;
import com.github.euler.core.SourceCommand;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.Behavior;

public class FileSourceTest extends AkkaTest {

    @Test
    public void testSingleFileAsSource() throws Exception {
        File file = File.createTempFile("test", ".txt");
        file.createNewFile();

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(file.toURI(), probe.ref());

        Behavior<SourceCommand> sourceBehavior = FileSource.create();
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(file.toURI(), response.uri);
        assertEquals(file.toURI(), response.itemURI);

        probe.expectMessageClass(ScanFinished.class);
    }

    @Test
    public void testFileInsideDir() throws Exception {
        File root = Files.createTempDirectory("test").toFile();
        File dir = new File(root, "dir");
        dir.mkdirs();
        File file = new File(dir, "item.txt");
        file.createNewFile();

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(dir.toURI(), probe.ref());

        Behavior<SourceCommand> sourceBehavior = FileSource.create();
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(dir.toURI(), response.uri);
        assertEquals(dir.toURI(), response.itemURI);

        response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(dir.toURI(), response.uri);
        assertEquals(file.toURI(), response.itemURI);

        probe.expectMessageClass(ScanFinished.class);
    }

    @Test
    public void testCustomVisitor() throws Exception {
        File file = File.createTempFile("test", ".txt");
        file.createNewFile();

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(file.toURI(), probe.ref());

        Behavior<SourceCommand> sourceBehavior = FileSource.create();
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(file.toURI(), response.uri);
        assertEquals(file.toURI(), response.itemURI);

        probe.expectMessageClass(ScanFinished.class);
    }

}
