package com.github.euler.file;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.EulerHooks;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

public class PathDeleteConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "path-delete";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext ctx, TypesConfigConverter typesConfigConverter) {
        Config config = ((ConfigObject) value).toConfig();
        config = getConfig(config);
        String path = config.getString("path");
        boolean failSafe = config.getBoolean("fail-safe");
        EulerHooks hooks = ctx.getRequired(EulerHooks.class);
        hooks.registerCloseable(new PathDelete(path, failSafe));
        return ctx;
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("pathdelete.conf");
        return ConfigFactory.parseURL(resource);
    }

}
