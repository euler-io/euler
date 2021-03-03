package com.github.euler.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.junit.Test;

import com.github.euler.core.EulerCommand;
import com.github.euler.core.JobItemFound;
import com.github.euler.core.JobToScan;
import com.github.euler.core.PausableSourceExecution;
import com.github.euler.core.ScanFinished;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.SourceExecution;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.Behavior;

public class FileSourceTest extends AkkaTest {

    @Test
    public void testSingleFileAsSource() throws Exception {
        File file = File.createTempFile("test", ".txt");
        file.createNewFile();

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(file.toURI(), probe.ref());

        Behavior<SourceCommand> sourceBehavior = SourceExecution.create(new FileSource());
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(file.toURI(), response.uri);
        assertEquals(file.toURI(), response.itemURI);

        probe.expectMessageClass(ScanFinished.class);
    }

    @Test
    public void testRegexIgnore() throws Exception {
        File root = Files.createTempDirectory("euler").toFile();

        File file1 = new File(root, "temp.txt");
        file1.createNewFile();

        File file2 = new File(root, "temp.pdf");
        file2.createNewFile();

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(root.toURI(), probe.ref());

        FileSource source = FileSource.builder()
                .setRegex(Pattern.compile(".+\\.txt$"))
                .setNotifyDirectories(false)
                .build();
        Behavior<SourceCommand> sourceBehavior = SourceExecution.create(source);
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(root.toURI(), response.uri);
        assertEquals(file1.toURI(), response.itemURI);

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

        Behavior<SourceCommand> sourceBehavior = PausableSourceExecution.create(new FileSource(1));
        testKit.spawn(sourceBehavior).tell(msg);

        JobItemFound response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(dir.toURI(), response.uri);
        assertEquals(dir.toURI(), response.itemURI);

        response = probe.expectMessageClass(JobItemFound.class);
        assertEquals(dir.toURI(), response.uri);
        assertEquals(file.toURI(), response.itemURI);

        probe.expectMessageClass(ScanFinished.class);
    }

}
