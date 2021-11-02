package com.github.euler.video;

import java.net.URL;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class FastFFmpegVideoThumbnailTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "ffmpeg-video-thumbnail";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        InputFactory inputFactory = typesConfigConverter.convert(AbstractInputFactoryConfigConverter.TYPE, config.getValue("input-factory"), ctx);
        OutputFactory outputFactory = typesConfigConverter.convert(AbstractOutputFactoryConfigConverter.TYPE, config.getValue("output-factory"), ctx);
        StorageStrategy storageStrategy = typesConfigConverter.convert(AbstractStorageStrategyConfigConverter.TYPE, config.getValue("storage-strategy"), ctx);
        int width = config.getInt("width");
        int height = config.getInt("height");
        float position = config.getNumber("position").floatValue();
        String field = config.getString("field");
        String[] additionalArgs = config.getStringList("args").stream().toArray(s -> new String[s]);
        return new FastFFmpegVideoThumbnailTask(getName(config, tasksConfigConverter),
                inputFactory,
                outputFactory,
                storageStrategy,
                width,
                height,
                position,
                field,
                additionalArgs);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = FastFFmpegVideoThumbnailTaskConfigConverter.class.getClassLoader().getResource("ffmpegthumbnail.conf");
        return ConfigFactory.parseURL(resource);
    }

}
