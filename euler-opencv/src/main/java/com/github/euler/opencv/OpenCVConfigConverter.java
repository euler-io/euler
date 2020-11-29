package com.github.euler.opencv;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.ConfigValue;

import nu.pattern.OpenCV;

public class OpenCVConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "opencv";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typesConfigConverter) {
        OpenCV.loadShared();
        return ConfigContext.EMPTY;
    }

    @Override
    public String getDescription() {
        return "Load OpenCV libraries.";
    }

}
