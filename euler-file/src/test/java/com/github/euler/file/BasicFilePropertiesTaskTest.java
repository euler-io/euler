package com.github.euler.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.core.JobTaskToProcess;

public class BasicFilePropertiesTaskTest extends AkkaTest {

    @Test
    public void testAcceptOnlyFileProtocol() throws Exception {
        BasicFilePropertiesTask task = new BasicFilePropertiesTask("task");
        assertFalse(task.accept(new JobTaskToProcess(new URI("not-file:///some/path"), new URI("not-file:///some/path/item"), null, null)));
        assertTrue(task.accept(new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), null, null)));
    }

}
