package com.github.euler.video;

import java.net.URL;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.IncludeExcludePattern;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class FfmpegVideoEncodeTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "ffmpeg-video-encode";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = getConfig(config);
        InputFactory inputFactory = typesConfigConverter.convert(AbstractInputFactoryConfigConverter.TYPE, config.getValue("input-factory"), ctx);
        OutputFactory outputFactory = typesConfigConverter.convert(AbstractOutputFactoryConfigConverter.TYPE, config.getValue("output-factory"), ctx);
        StorageStrategy storageStrategy = typesConfigConverter.convert(AbstractStorageStrategyConfigConverter.TYPE, config.getValue("storage-strategy"), ctx);
        String field = config.getString("field");
        IncludeExcludePattern mimePattern = IncludeExcludePattern.fromConfig(config.getConfig("mime-type"));
        String[] additionalArgs = config.getStringList("args").stream().toArray(s -> new String[s]);
        return new FfmpegVideoEncodeTask(getName(config, tasksConfigConverter),
                inputFactory,
                outputFactory,
                storageStrategy,
                field,
                mimePattern,
                additionalArgs);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("videoencode.conf");
        return ConfigFactory.parseURL(resource);
    }

}
