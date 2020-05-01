package com.github.euler.common;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import com.github.euler.config.AbstractFactory;
import com.github.euler.config.ConfigContext;
import com.github.euler.config.ConfigContext.Builder;
import com.github.euler.config.ContextCreator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class StreamFactoryFactory extends AbstractFactory<StreamFactory> implements ContextCreator {

    private Map<String, StreamFactoryCreator> creatorsMap;

    public StreamFactoryFactory() {
        super();
        this.loadTaskCreators();
    }

    private void loadTaskCreators() {
        creatorsMap = ServiceLoader.load(StreamFactoryCreator.class).stream()
                .map(Provider::get)
                .collect(Collectors.toMap(l -> l.type(), l -> l));
    }

    @Override
    public String type() {
        return "stream-factory";
    }

    @Override
    public ConfigContext create(ConfigValue v, ConfigContext ctx) {
        Builder builder = ConfigContext.builder();
        StreamFactory sf = this.createFromConfigWithContext(v, ctx);
        builder.put(StreamFactory.class, sf);
        return builder.build();
    }

    @Override
    protected StreamFactory create(String type, Config config, ConfigContext ctx) {
        return creatorsMap.get(type).create(config, ctx);
    }

}
