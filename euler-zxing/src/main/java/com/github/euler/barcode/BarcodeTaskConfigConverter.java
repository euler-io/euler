package com.github.euler.barcode;

import java.net.URL;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ConfigUtil;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.google.zxing.BarcodeFormat;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class BarcodeTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "barcode";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        String field = config.getString("field");
        BarcodeResultSerializer serializer = typesConfigConverter.convert(AbstractBarcodeResultSerializerConfigConverter.TYPE, config.getValue("serializer"), ctx);
        BarcodeFormat[] formats = ConfigUtil.getEnumSet(BarcodeFormat.class, config, "formats").stream().toArray(s -> new BarcodeFormat[s]);
        return new BarcodeTask(getName(config, tasksConfigConverter),
                sf,
                field,
                serializer,
                formats);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("barcode.conf");
        return ConfigFactory.parseURL(resource);
    }

}
