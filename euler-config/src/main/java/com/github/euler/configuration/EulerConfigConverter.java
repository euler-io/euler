package com.github.euler.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.BiFunction;

import com.github.euler.core.Euler;
import com.github.euler.core.EulerProcessor;
import com.github.euler.core.JobCommand;
import com.github.euler.core.JobExecution;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class EulerConfigConverter {

    private final Map<String, ContextConfigConverter> convertersMap = new HashMap<>();
    private final Map<String, TaskConfigConverter> taskConverterMap = new HashMap<>();
    private final ConfigContext ctx;
    private final List<TypeConfigConverter<?>> typeConverters = new ArrayList<>();
    private final List<EulerExtension> extensions = new ArrayList<>();

    public EulerConfigConverter() {
        this(ConfigContext.EMPTY, Thread.currentThread().getContextClassLoader());
    }

    public EulerConfigConverter(ClassLoader classLoader) {
        this(ConfigContext.EMPTY, classLoader);
    }

    public EulerConfigConverter(ConfigContext ctx) {
        this(ctx, Thread.currentThread().getContextClassLoader());
    }

    public EulerConfigConverter(ConfigContext ctx, ClassLoader classLoader) {
        this.ctx = ctx;
        load(classLoader);
    }

    private void load(ClassLoader classLoader) {
        ServiceLoader.load(EulerExtension.class, classLoader)
                .stream()
                .map(Provider::get)
                .forEach(this::register);
    }

    public EulerConfigConverter register(EulerExtension extension) {
        extensions.add(extension);
        extension.pathConverters().forEach(c -> convertersMap.put(c.path(), c));
        extension.taskConverters().forEach(c -> taskConverterMap.put(c.type(), c));
        typeConverters.addAll(extension.typeConverters());
        return this;
    }

    protected ConfigContext convertContext(Config config, ConfigContext ctx) {
        List<Entry<String, ConfigValue>> entries = new ArrayList<Map.Entry<String, ConfigValue>>(config.root().entrySet());
        Collections.sort(entries, new ConfigEntryComparator());
        return convertContext(entries, ctx);
    }

    protected ConfigContext convertContext(Collection<Entry<String, ConfigValue>> configEntries, ConfigContext ctx) {
        TasksConfigConverter tasksConverter = new TasksConfigConverter(taskConverterMap);
        SourceConfigConverter sourceConverter = new SourceConfigConverter();

        Map<String, ContextConfigConverter> converters = new HashMap<>();
        converters.put(tasksConverter.path(), tasksConverter);
        converters.put(sourceConverter.path(), sourceConverter);
        converters.putAll(convertersMap);

        ConfigContext.Builder builder = ConfigContext.builder();
        builder.putAll(ctx);
        for (Entry<String, ConfigValue> e : configEntries) {
            String path = e.getKey();
            if (converters.containsKey(path)) {
                ConfigValue value = e.getValue();
                ContextConfigConverter converter = converters.get(path);
                ConfigContext converted = converter.convert(value, builder.build(), new TypesConfigConverter(typeConverters));
                builder.putAll(converted);
            } else {
                throw new IllegalArgumentException(ContextConfigConverter.class.getSimpleName() + " not found for path '" + path + "'.");
            }
        }

        return builder.build();
    }

    public Behavior<JobCommand> create(Config config) {
        return create(config, this.ctx);
    }

    public Behavior<JobCommand> create(Config config, ConfigContext ctx) {
        return create(config, ctx, (s, p) -> JobExecution.create(s, p));
    }

    public <R> R create(Config config, BiFunction<Behavior<SourceCommand>, Behavior<ProcessorCommand>, R> func) {
        return create(config, this.ctx, func);
    }

    public <R> R create(Config config, ConfigContext ctx, BiFunction<Behavior<SourceCommand>, Behavior<ProcessorCommand>, R> func) {
        ctx = convertContext(config, this.ctx.merge(ctx));
        return create(ctx, func);
    }

    @SuppressWarnings("unchecked")
    private <R> R create(ConfigContext ctx, BiFunction<Behavior<SourceCommand>, Behavior<ProcessorCommand>, R> func) {
        List<Task> tasks = (List<Task>) ctx.getRequired(TasksConfigConverter.TASKS);
        Behavior<SourceCommand> sourceBehavior = (Behavior<SourceCommand>) ctx.getRequired(SourceConfigConverter.SOURCE);
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks.toArray(new Task[tasks.size()]));
        return func.apply(sourceBehavior, processorBehavior);
    }

    @SuppressWarnings("unchecked")
    public Euler createEuler(Config config, ConfigContext ctx) {
        ctx = convertContext(config, ctx);
        List<Task> tasks = (List<Task>) ctx.getRequired(TasksConfigConverter.TASKS);
        Behavior<SourceCommand> sourceBehavior = (Behavior<SourceCommand>) ctx.getRequired(SourceConfigConverter.SOURCE);
        return new Euler(sourceBehavior, tasks.toArray(new Task[tasks.size()]));
    }

    public Euler createEuler(Config config) {
        return createEuler(config, this.ctx);
    }

    public List<EulerExtension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

}
