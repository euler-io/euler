package com.github.euler.config;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import com.typesafe.config.ConfigValue;

public class ContextFactory {

    private Map<String, ContextCreator> creatorsMap;

    private ContextFactory(ClassLoader classLoader) {
        super();
        this.loadContextCreators(classLoader);
    }

    private void loadContextCreators(ClassLoader classLoader) {
        creatorsMap = ServiceLoader.load(ContextCreator.class, classLoader).stream()
                .map(Provider::get)
                .collect(Collectors.toMap(l -> l.type(), l -> l));
    }

    protected ConfigContext create(String type, ConfigValue v, ConfigContext ctx) {
        ContextCreator creator = creatorsMap.get(type);
        return creator.create(v, ctx);
    }

    public static ContextFactory load() {
        return load(Thread.currentThread().getContextClassLoader());
    }

    public static ContextFactory load(ClassLoader classLoader) {
        return new ContextFactory(classLoader);
    }

}
