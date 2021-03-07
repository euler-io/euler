package com.github.euler.preview;

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

public class PreviewTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "preview";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());

        PreviewConfig previewConfig = new PreviewConfig();
        config.getList("generators").forEach(c -> {
            PreviewGenerator g = typesConfigConverter.convert(AbstractPreviewGeneratorConfigConverter.PREVIEW_GENERATOR, c, ctx);
            previewConfig.add(g);
        });
        PreviewContext previewContext = new PreviewContext();

        String name = getName(config, tasksConfigConverter);
        EulerPreview preview = new EulerPreview(previewConfig);
        String formatName = config.getString("format");
        StreamFactory streamFactory = ctx.getRequired(StreamFactory.class);
        StorageStrategy storageStrategy = typesConfigConverter.convert("storage-strategy", config.getValue("storage-strategy"), ctx);

        return new PreviewTask(name, preview, previewContext, formatName, streamFactory, storageStrategy);
    }

    protected Config getDefaultConfig() {
        URL resource = PreviewTaskConfigConverter.class.getClassLoader().getResource("previewtask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
