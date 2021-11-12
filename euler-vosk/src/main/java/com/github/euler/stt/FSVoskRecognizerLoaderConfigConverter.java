package com.github.euler.stt;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class FSVoskRecognizerLoaderConfigConverter extends AbstractVoskRecognizerLoaderConfigConverter {

    @Override
    public String configType() {
        return "fs";
    }

    @Override
    public VoskRecognizerLoader convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        String modelPath = config.getString("model-path");
        float sampleRate = config.getNumber("sample-rate").floatValue();
        String speakerModelPath = config.hasPath("speaker-model-path") ? config.getString("speaker-model-path") : null;
        return new FSVoskRecognizerLoader(modelPath, sampleRate, speakerModelPath);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = getClass().getClassLoader().getResource("fsvoskmodelloader.conf");
        return ConfigFactory.parseURL(resource);
    }

}
