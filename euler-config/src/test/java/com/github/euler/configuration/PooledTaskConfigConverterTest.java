package com.github.euler.configuration;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.core.PooledTask;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PooledTaskConfigConverterTest {

    private static TasksConfigConverter tasksConfigConverter;
    private static PooledTaskConfigConverter converter;

    @BeforeClass
    public static void setup() {
        converter = new PooledTaskConfigConverter();
        TestConfigConverter testConfigConverter = new TestConfigConverter();
        Map<String, TaskConfigConverter> taskConverterMap = new HashMap<>();
        taskConverterMap.put(testConfigConverter.type(), testConfigConverter);
        taskConverterMap.put(converter.type(), converter);
        tasksConfigConverter = new TasksConfigConverter(taskConverterMap);
    }

    @Test
    public void testCreateNoName() {
        Config config = ConfigFactory.parseString("{task = \"test\", size = 1}");
        PooledTask task = (PooledTask) converter.convert(config, ConfigContext.EMPTY, null, tasksConfigConverter);
        assertEquals(1, task.getSize());
    }

    @Test
    public void testCreateWithName() {
        Config config = ConfigFactory.parseString("{task = \"test\", size = 1, name = \"test-task\"}");
        PooledTask task = (PooledTask) converter.convert(config, ConfigContext.EMPTY, null, tasksConfigConverter);
        assertEquals(1, task.getSize());
        assertEquals("test-task", task.name());
    }

}
