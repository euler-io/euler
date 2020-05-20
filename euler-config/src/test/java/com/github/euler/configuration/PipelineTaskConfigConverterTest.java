package com.github.euler.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.core.PipelineTask;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PipelineTaskConfigConverterTest {

    private static TasksConfigConverter tasksConfigConverter;
    private static PipelineTaskConfigConverter converter;

    @BeforeClass
    public static void setup() {
        converter = new PipelineTaskConfigConverter();
        TestConfigConverter testConfigConverter = new TestConfigConverter();
        Map<String, TaskConfigConverter> taskConverterMap = new HashMap<>();
        taskConverterMap.put(testConfigConverter.type(), testConfigConverter);
        taskConverterMap.put(converter.type(), converter);
        tasksConfigConverter = new TasksConfigConverter(taskConverterMap);
    }

    @Test
    public void testCreateWithName() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}], name = \"test-task\"}");
        PipelineTask task = (PipelineTask) converter.convert(config, ConfigContext.EMPTY, null, tasksConfigConverter);
        assertEquals(1, task.getTasks().length);
        assertEquals("test-task", task.name());
    }

    @Test
    public void testCreateNoName() {
        Config config = ConfigFactory.parseString("{tasks = [{type = \"test\"}]}");
        PipelineTask task = (PipelineTask) converter.convert(config, ConfigContext.EMPTY, null, tasksConfigConverter);
        assertEquals(1, task.getTasks().length);
        assertNotNull(task.name());
    }

}
