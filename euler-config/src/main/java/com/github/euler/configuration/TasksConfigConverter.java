package com.github.euler.configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class TasksConfigConverter implements ContextConfigConverter {

    private static final String TYPE = "type";
    public static final String TASKS = "tasks";

    private final Map<String, TaskConfigConverter> taskConverterMap;
    private int taskCounter = 0;

    public TasksConfigConverter(Map<String, TaskConfigConverter> taskConverterMap) {
        super();
        this.taskConverterMap = taskConverterMap;
    }

    @Override
    public String path() {
        return TASKS;
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        switch (value.valueType()) {
        case LIST:
            ConfigList configList = (ConfigList) value;
            List<Task> tasks = convert(configList, configContext, typeConfigConverter);
            return ConfigContext.builder().put(TASKS, tasks).build();
        default:
            return null;
        }
    }

    public List<Task> convert(ConfigList configList, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return configList.stream()
                .map(c -> convertTask(c, configContext, typeConfigConverter))
                .collect(Collectors.toList());
    }

    public Task convertTask(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String type;
        Config config;
        switch (value.valueType()) {
        case STRING:
            type = value.unwrapped().toString();
            config = ConfigFactory.empty();
            break;
        case OBJECT:
            config = ((ConfigObject) value).toConfig();
            type = config.getString(TYPE);
            break;
        default:
            return null;
        }
        TaskConfigConverter converter = taskConverterMap.get(type);
        Objects.requireNonNull(converter, () -> "Converter not found for task " + type);
        return converter.convert(config, configContext, typeConfigConverter, this);
    }

    public String createTaskName() {
        return "task" + taskCounter++;
    }

}
