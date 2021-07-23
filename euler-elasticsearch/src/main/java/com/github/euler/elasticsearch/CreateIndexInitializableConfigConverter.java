package com.github.euler.elasticsearch;

import java.net.URL;

import org.elasticsearch.client.RestHighLevelClient;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.EulerHooks;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

public class CreateIndexInitializableConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "create-index";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext ctx, TypesConfigConverter typesConfigConverter) {
        Config config = ((ConfigObject) value).toConfig();
        config = getConfig(config);
        RestHighLevelClient client = typesConfigConverter.convert(AbstractElasticsearchClientConfigConverter.TYPE, config.getValue("elasticsearch-client"), ctx);
        EulerHooks hooks = ctx.getRequired(EulerHooks.class);
        hooks.registerInitializable(new CreateIndexInitializable(client, config));
        return ctx;
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("createindex.conf");
        return ConfigFactory.parseURL(resource);
    }
}
