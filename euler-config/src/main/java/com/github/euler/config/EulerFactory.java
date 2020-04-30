package com.github.euler.config;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class EulerFactory {

    private static final String SOURCE = "source";
    private static final String TASKS = "tasks";
    private static final Set<String> RESERVED_PATHS = Collections.unmodifiableSet(Set.of(SOURCE, TASKS));

    private final TaskFactory taskFactory;
    private final SourceFactory sourceFactory;
    private final ContextFactory contextFactory;

    public EulerFactory(TaskFactory taskFactory, SourceFactory sourceFactory, ContextFactory contextFactory) {
        super();
        this.taskFactory = taskFactory;
        this.sourceFactory = sourceFactory;
        this.contextFactory = contextFactory;
    }

    private EulerFactory(ClassLoader classLoader) {
        this(TaskFactory.load(classLoader), SourceFactory.load(classLoader), ContextFactory.load(classLoader));
    }

    public static EulerFactory load() {
        return load(Thread.currentThread().getContextClassLoader());
    }

    public static EulerFactory load(ClassLoader classLoader) {
        return new EulerFactory(classLoader);
    }

    public List<Task> createTasks(ConfigList configList, ConfigContext ctx) {
        return configList.stream()
                .map(v -> createTask(v, ctx))
                .collect(Collectors.toList());
    }

    public List<Task> createTasks(ConfigList configList) {
        return createTasks(configList, ConfigContext.EMPTY);
    }

    public Task createTask(ConfigValue value, ConfigContext ctx) {
        return taskFactory.create(value, ctx);
    }

    public Task createTask(ConfigValue value) {
        return createTask(value, ConfigContext.EMPTY);
    }

    public Behavior<SourceCommand> createSource(ConfigValue value) {
        return createSource(value, ConfigContext.EMPTY);
    }

    private Behavior<SourceCommand> createSource(ConfigValue value, ConfigContext ctx) {
        return sourceFactory.create(value, ctx);
    }

    public Behavior<JobCommand> create(Config config) {
        ConfigContext ctx = createContext(config);
        Task[] tasks = createTasks(config.getList(TASKS), ctx).stream().toArray(s -> new Task[s]);
        Behavior<SourceCommand> sourceBehavior = createSource(config.getValue(SOURCE), ctx);
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks);
        return JobExecution.create(sourceBehavior, processorBehavior);
    }

    public Euler createEuler(Config config) {
        ConfigContext ctx = createContext(config);
        Task[] tasks = createTasks(config.getList(TASKS), ctx).stream().toArray(s -> new Task[s]);
        Behavior<SourceCommand> sourceBehavior = createSource(config.getValue(SOURCE), ctx);
        return new Euler(sourceBehavior, tasks);
    }

    public ConfigContext createContext(Config config) {
        ConfigContext ctx = ConfigContext.EMPTY;
        ConfigObject obj = config.root();
        for (Entry<String, ConfigValue> entry : obj.entrySet()) {
            String key = entry.getKey();
            if (!RESERVED_PATHS.contains(key)) {
                ConfigValue value = entry.getValue();
                ctx = ctx.merge(contextFactory.create(key, value, ctx));
            }
        }
        return ctx;
    }

}
