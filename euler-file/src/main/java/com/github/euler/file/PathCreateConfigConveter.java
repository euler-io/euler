package com.github.euler.file;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.EulerHooks;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class PathCreateConfigConveter implements ContextConfigConverter {

    @Override
    public String path() {
        return "path-create";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext ctx, TypesConfigConverter typesConfigConverter) {
        Config config = ((ConfigObject) value).toConfig();
        String path = config.getString("path");
        EulerHooks hooks = ctx.getRequired(EulerHooks.class);
        hooks.registerInitializable(new PathCreate(path));
        return ctx;
    }

}
