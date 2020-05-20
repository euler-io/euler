package com.github.euler.tika;

import org.apache.tika.detect.Detector;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.ConfigValue;

public class TikaContextConfigConverter implements ContextConfigConverter {

    @Override
    public String path() {
        return "tika";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        ConfigContext.Builder builder = ConfigContext.builder();

        AutoDetectParser parser = new AutoDetectParser();
        builder.put(Parser.class, parser);
        builder.put(Detector.class, parser.getDetector());

        return builder.build();
    }

}
