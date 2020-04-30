package com.github.euler.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.github.euler.core.Euler;
import com.github.euler.core.JobCommand;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class EulerFactoryTest {

    @Test
    public void testCreateTasks() {
        Config config = ConfigFactory.parseString("{tasks = [\"test\"]}");
        ConfigList configList = config.getList("tasks");
        EulerFactory factory = EulerFactory.load();
        List<Task> tasks = factory.createTasks(configList);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testCreateTasksWithType() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}]}");
        ConfigList configList = config.getList("tasks");
        EulerFactory factory = EulerFactory.load();
        List<Task> tasks = factory.createTasks(configList);
        assertEquals(1, tasks.size());
    }

    @Test
    public void testCreateTasksMixed() {
        Config config = ConfigFactory.parseString("{tasks = [\"test\", {type = \"test\"}]}");
        ConfigList configList = config.getList("tasks");
        EulerFactory factory = EulerFactory.load();
        List<Task> tasks = factory.createTasks(configList);
        assertEquals(2, tasks.size());
    }

    @Test
    public void testCreateContext() {
        Config config = ConfigFactory.parseString("{test = { test-key = test-value }}");
        EulerFactory factory = EulerFactory.load();
        ConfigContext ctx = factory.createContext(config);
        assertEquals("test-value", ctx.get("test-key"));
    }

    @Test
    public void testCreateSource() {
        Config config = ConfigFactory.parseString("{source = \"test\"}");
        EulerFactory factory = EulerFactory.load();
        ConfigValue value = config.getValue("source");
        Behavior<SourceCommand> sourceBehavior = factory.createSource(value);
        assertNotNull(sourceBehavior);
    }

    @Test
    public void testCreateEuler() {
        Config config = ConfigFactory.parseString("{tasks = [\"test\"], source = \"test\"}");
        EulerFactory factory = EulerFactory.load();
        Euler euler = factory.createEuler(config);
        assertNotNull(euler);
        assertNotNull(euler.getSourceBehavior());
        assertEquals(1, euler.getTasks().length);
    }

    @Test
    public void testCreateJob() {
        Config config = ConfigFactory.parseString("{tasks = [\"test\"], source = \"test\"}");
        EulerFactory factory = EulerFactory.load();
        Behavior<JobCommand> jobBehavior = factory.create(config);
        assertNotNull(jobBehavior);
    }

}
