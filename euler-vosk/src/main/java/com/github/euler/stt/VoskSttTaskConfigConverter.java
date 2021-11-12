package com.github.euler.stt;

import java.net.URL;

import org.vosk.Recognizer;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.EulerHooks;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class VoskSttTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "stt-vosk";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        StorageStrategy storageStrategy = typesConfigConverter.convert(AbstractStorageStrategyConfigConverter.TYPE, config.getValue("storage-strategy"), ctx);
        Recognizer recognizer = typesConfigConverter.convert(AbstractVoskRecognizerLoaderConfigConverter.TYPE, config.getValue("recognizer"), ctx);

        EulerHooks hooks = ctx.getRequired(EulerHooks.class);
        hooks.registerCloseable(recognizer);

        return new VoskSttTask(getName(config, tasksConfigConverter),
                sf,
                storageStrategy,
                recognizer);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("sttvosk.conf");
        return ConfigFactory.parseURL(resource);
    }

}
