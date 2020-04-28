package com.github.euler.config;

import java.util.List;
import java.util.stream.Collectors;

import com.github.euler.core.Euler;
import com.github.euler.core.EulerProcessor;
import com.github.euler.core.JobCommand;
import com.github.euler.core.JobExecution;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class EulerFactory {

    private static final String SOURCE = "source";
    private static final String TASKS = "tasks";

    private TaskFactory taskFactory;
    private SourceFactory sourceFactory;

    public EulerFactory(TaskFactory taskFactory, SourceFactory sourceFactory) {
        super();
        this.taskFactory = taskFactory;
        this.sourceFactory = sourceFactory;
    }

    private EulerFactory(ClassLoader classLoader) {
        this(TaskFactory.load(classLoader), SourceFactory.load(classLoader));
    }

    public static EulerFactory load() {
        return load(Thread.currentThread().getContextClassLoader());
    }

    public static EulerFactory load(ClassLoader classLoader) {
        return new EulerFactory(classLoader);
    }

    public List<Task> createTasks(ConfigList configList) {
        return configList.stream()
                .map(v -> taskFactory.create(v))
                .collect(Collectors.toList());
    }

    public Behavior<SourceCommand> createSource(ConfigValue value) {
        return sourceFactory.create(value);
    }

    public Behavior<JobCommand> create(Config config) {
        Task[] tasks = createTasks(config.getList(TASKS)).stream().toArray(s -> new Task[s]);
        Behavior<SourceCommand> sourceBehavior = createSource(config.getValue(SOURCE));
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks);
        return JobExecution.create(sourceBehavior, processorBehavior);
    }

    public Euler createEuler(Config config) {
        Task[] tasks = createTasks(config.getList(TASKS)).stream().toArray(s -> new Task[s]);
        Behavior<SourceCommand> sourceBehavior = createSource(config.getValue(SOURCE));
        return new Euler(sourceBehavior, tasks);
    }

}
