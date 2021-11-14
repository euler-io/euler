package com.github.euler.stt;

import java.io.IOException;
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
import com.github.euler.core.FieldType;
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

        VoskRecognizerLoader recognizerLoader = typesConfigConverter.convert(AbstractVoskRecognizerLoaderConfigConverter.TYPE, config.getValue("recognizer"), ctx);
        Recognizer recognizer;
        try {
            recognizer = recognizerLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EulerHooks hooks = ctx.getRequired(EulerHooks.class);
        hooks.registerCloseable(recognizer);

        String inputField = config.hasPath("input-field") ? config.getString("input-field") : null;
        FieldType inputFieldType = config.getEnum(FieldType.class, "input-field-type");

        return new VoskSttTask(getName(config, tasksConfigConverter),
                sf,
                storageStrategy,
                recognizer,
                inputField,
                inputFieldType);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("sttvosk.conf");
        return ConfigFactory.parseURL(resource);
    }

}
