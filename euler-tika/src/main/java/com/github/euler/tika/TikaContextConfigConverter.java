package com.github.euler.tika;

import java.io.IOException;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class TikaContextConfigConverter implements ContextConfigConverter {

    private static final String TIKA_CONFIG_PATH = "tika-config-path";

    @Override
    public String path() {
        return "tika";
    }

    @Override
    public ConfigContext convert(ConfigValue value, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        Config config = ((ConfigObject) value).toConfig();
        ConfigContext.Builder builder = ConfigContext.builder();

        TikaConfig tikaConfig;
        if (config.hasPath(TIKA_CONFIG_PATH)) {
            try {
                tikaConfig = new TikaConfig(config.getString(TIKA_CONFIG_PATH));
            } catch (TikaException | IOException | SAXException e) {
                throw new RuntimeException(e);
            }
        } else {
            tikaConfig = TikaConfig.getDefaultConfig();
        }

        builder.put(Parser.class, tikaConfig.getParser());
        builder.put(Detector.class, tikaConfig.getDetector());

        return builder.build();
    }

}
