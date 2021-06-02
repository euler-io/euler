package com.github.euler.tika;

import java.net.URL;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class StripHTMLTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "strip-html";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        String name = getName(config, tasksConfigConverter);
        StorageStrategy parsedContentStrategy = typesConfigConverter.convert("storage-strategy", config.getValue("parsed-storage-strategy"), ctx);
        return new StripHTMLTask(name, sf, parsedContentStrategy);
    }

    protected Config getDefaultConfig() {
        URL resource = StripHTMLTaskConfigConverter.class.getClassLoader().getResource("parsetask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
