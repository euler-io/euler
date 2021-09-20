package com.github.euler.tika;

import java.net.URL;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class LanguageDetectTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "language-detect";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        String name = getName(config, tasksConfigConverter);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        long maxBytes = config.getLong("max-bytes");
        int maxLangs = config.getInt("max-langs");
        String field = config.getString("field");
        return new LanguageDetectTask(name, sf, maxBytes, maxLangs, field);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("languagedetect.conf");
        return ConfigFactory.parseURL(resource);
    }

}
