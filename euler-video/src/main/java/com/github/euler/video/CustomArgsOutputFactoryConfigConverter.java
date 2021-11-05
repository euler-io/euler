package com.github.euler.video;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class CustomArgsOutputFactoryConfigConverter extends AbstractOutputFactoryConfigConverter {

    @Override
    public String configType() {
        return "custom-args";
    }

    @Override
    public OutputFactory convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter) {
        config = getConfig(config);
        OutputFactory wrapped = typesConfigConverter.convert(AbstractOutputFactoryConfigConverter.TYPE, config.getValue("output-factory"), ctx);
        String[] inputArgs = config.getStringList("args").stream().toArray(s -> new String[s]);
        return new CustomArgsOutputFactory(wrapped, inputArgs);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = FastFFmpegVideoThumbnailTaskConfigConverter.class.getClassLoader().getResource("customargsoutput.conf");
        return ConfigFactory.parseURL(resource);
    }

}
