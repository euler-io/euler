package com.github.euler.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.euler.core.ConcurrentTask;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConcurrentTaskCreatorTest {

    @Test
    public void testCreateWithName() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}], name = \"test-task\"}");
        TaskFactory factory = TaskFactory.load();
        ConcurrentTaskCreator creator = new ConcurrentTaskCreator();
        ConcurrentTask task = (ConcurrentTask) creator.create(config, factory, ConfigContext.EMPTY);
        assertEquals(1, task.getTasks().length);
        assertEquals("test-task", task.name());
    }

    @Test
    public void testCreateNoName() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}]}");
        TaskFactory factory = TaskFactory.load();
        ConcurrentTaskCreator creator = new ConcurrentTaskCreator();
        ConcurrentTask task = (ConcurrentTask) creator.create(config, factory, ConfigContext.EMPTY);
        assertEquals(1, task.getTasks().length);
        assertNotNull(task.name());
    }

}
