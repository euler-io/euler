package com.github.euler.configuration;

import java.util.ArrayList;
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
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class EulerConfigConverter {

    private final Map<String, ContextConfigConverter> convertersMap = new HashMap<>();
    private final Map<String, TaskConfigConverter> taskConverterMap = new HashMap<>();
    private final List<TypeConfigConverter<?>> typeConverters = new ArrayList<>();
    private final List<EulerExtension> extensions = new ArrayList<>();

    public EulerConfigConverter() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public EulerConfigConverter(ClassLoader classLoader) {
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

    protected ConfigContext convertContext(Config config) {
        ConfigObject configObject = config.root();

        TasksConfigConverter tasksConverter = new TasksConfigConverter(taskConverterMap);
        SourceConfigConverter sourceConverter = new SourceConfigConverter();

        Map<String, ContextConfigConverter> converters = new HashMap<>();
        converters.put(tasksConverter.path(), tasksConverter);
        converters.put(sourceConverter.path(), sourceConverter);
        converters.putAll(convertersMap);

        ConfigContext.Builder builder = ConfigContext.builder();
        for (Entry<String, ConfigValue> e : configObject.entrySet()) {
            String path = e.getKey();
            ConfigValue value = e.getValue();
            ContextConfigConverter converter = converters.get(path);
            ConfigContext converted = converter.convert(value, builder.build(), new TypesConfigConverter(typeConverters));
            builder.putAll(converted);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public Behavior<JobCommand> create(Config config) {
        ConfigContext ctx = convertContext(config);
        List<Task> tasks = (List<Task>) ctx.getRequired(TasksConfigConverter.TASKS);
        Behavior<SourceCommand> sourceBehavior = (Behavior<SourceCommand>) ctx.getRequired(SourceConfigConverter.SOURCE);
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks.toArray(new Task[tasks.size()]));
        return JobExecution.create(sourceBehavior, processorBehavior);
    }

    @SuppressWarnings("unchecked")
    public <R> R create(Config config, BiFunction<Behavior<SourceCommand>, Behavior<ProcessorCommand>, R> func) {
        ConfigContext ctx = convertContext(config);
        List<Task> tasks = (List<Task>) ctx.getRequired(TasksConfigConverter.TASKS);
        Behavior<SourceCommand> sourceBehavior = (Behavior<SourceCommand>) ctx.getRequired(SourceConfigConverter.SOURCE);
        Behavior<ProcessorCommand> processorBehavior = EulerProcessor.create(tasks.toArray(new Task[tasks.size()]));
        return func.apply(sourceBehavior, processorBehavior);
    }

    @SuppressWarnings("unchecked")
    public Euler createEuler(Config config) {
        ConfigContext ctx = convertContext(config);
        List<Task> tasks = (List<Task>) ctx.getRequired(TasksConfigConverter.TASKS);
        Behavior<SourceCommand> sourceBehavior = (Behavior<SourceCommand>) ctx.getRequired(SourceConfigConverter.SOURCE);
        return new Euler(sourceBehavior, tasks.toArray(new Task[tasks.size()]));
    }

    public List<EulerExtension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

}
