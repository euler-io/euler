package com.github.euler.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class TaskFactoryTest {

    @Test
    public void testCreateTask() {
        Config config = ConfigFactory.parseString("{tasks = [\"test\"]}");
        ConfigValue value = config.getList("tasks").get(0);
        TaskFactory factory = TaskFactory.load();
        Task task = factory.create(value);
        assertNotNull(task);
    }

    @Test
    public void testCreateTaskWithType() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}]}");
        ConfigValue value = config.getList("tasks").get(0);
        TaskFactory factory = TaskFactory.load();
        Task task = factory.create(value);
        assertNotNull(task);
    }

}
