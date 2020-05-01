package com.github.euler.config;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class TaskFactory extends AbstractFactory<Task> {

    private Map<String, TaskCreator> creatorsMap;
    private int taskCounter = 0;

    private TaskFactory(ClassLoader classLoader) {
        super();
        this.loadTaskCreators(classLoader);
    }

    private void loadTaskCreators(ClassLoader classLoader) {
        creatorsMap = ServiceLoader.load(TaskCreator.class, classLoader).stream()
                .map(Provider::get)
                .collect(Collectors.toMap(l -> l.type(), l -> l));
    }

    public static TaskFactory load() {
        return load(Thread.currentThread().getContextClassLoader());
    }

    public static TaskFactory load(ClassLoader classLoader) {
        return new TaskFactory(classLoader);
    }

    public Task create(String type, Config config, ConfigContext ctx) {
        TaskCreator creator = creatorsMap.get(type);
        return creator.create(config, this, ctx);
    }

    public String createTaskName() {
        return "task" + taskCounter++;
    }

    public Task create(ConfigValue value) {
        return this.createFromConfig(value);
    }

    public Task create(ConfigValue value, ConfigContext ctx) {
        return this.createFromConfigWithContext(value, ctx);
    }

}
