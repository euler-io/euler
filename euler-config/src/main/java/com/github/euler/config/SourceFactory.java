package com.github.euler.config;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class SourceFactory extends AbstractFactory<Behavior<SourceCommand>> {

    private Map<String, SourceCreator> loadersMap;

    private SourceFactory(ClassLoader classLoader) {
        super();
        this.loadSourceLoaders(classLoader);
    }

    private void loadSourceLoaders(ClassLoader classLoader) {
        ServiceLoader<SourceCreator> loaders = ServiceLoader.load(SourceCreator.class, classLoader);
        this.loadersMap = loaders.stream().map(Provider::get).collect(Collectors.toMap(l -> l.type(), l -> l));
    }

    public Behavior<SourceCommand> create(String type, Config config) {
        SourceCreator sourceLoader = loadersMap.get(type);
        return sourceLoader.create(config);
    }

    public static SourceFactory load() {
        return load(Thread.currentThread().getContextClassLoader());
    }

    public static SourceFactory load(ClassLoader classLoader) {
        SourceFactory factory = new SourceFactory(classLoader);
        return factory;
    }

}
