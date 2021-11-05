package com.github.euler.video;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class CustomArgsInputFactoryConfigConverter extends AbstractInputFactoryConfigConverter {

    @Override
    public String configType() {
        return "custom-args";
    }

    @Override
    public InputFactory convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter) {
        config = getConfig(config);
        InputFactory wrapped = typesConfigConverter.convert(AbstractInputFactoryConfigConverter.TYPE, config.getValue("input-factory"), ctx);
        String[] inputArgs = config.getStringList("args").stream().toArray(s -> new String[s]);
        return new CustomArgsInputFactory(wrapped, inputArgs);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("customargsinput.conf");
        return ConfigFactory.parseURL(resource);
    }

}
