package com.github.euler.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.euler.core.PooledTask;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PooledTaskCreatorTest {

    @Test
    public void testCreateNoName() {
        Config config = ConfigFactory.parseString("{task = \"test\", size = 1}");
        TaskFactory factory = TaskFactory.load();
        TaskCreator creator = new PooledTaskCreator();
        PooledTask task = (PooledTask) creator.create(config, factory);
        assertEquals(1, task.getSize());
    }

    @Test
    public void testCreateWithName() {
        Config config = ConfigFactory.parseString("{task = \"test\", size = 1, name = \"test-task\"}");
        TaskFactory factory = TaskFactory.load();
        TaskCreator creator = new PooledTaskCreator();
        PooledTask task = (PooledTask) creator.create(config, factory);
        assertEquals(1, task.getSize());
        assertEquals("test-task", task.name());
    }

}
