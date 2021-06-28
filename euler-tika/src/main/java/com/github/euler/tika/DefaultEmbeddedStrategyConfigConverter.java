package com.github.euler.tika;

import java.net.URL;
import java.util.List;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class DefaultEmbeddedStrategyConfigConverter extends AbstractEmbeddedStrategeyConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public EmbeddedStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        int maxDepth = config.getInt("max-depth");
        List<String> includeParseEmbeddedRegex = getListOrString("parse.include-regex", config);
        List<String> excludeParseEmbeddedRegex = getListOrString("parse.exclude-regex", config);
        List<String> includeExtractEmbeddedRegex = getListOrString("extract.include-regex", config);
        List<String> excludeExtractEmbeddedRegex = getListOrString("extract.exclude-regex", config);
        String mimeTypeField = config.getString("mime-type-field");
        boolean outputName = config.getBoolean("output-name");
        return new DefaultEmbeddedStrategy(maxDepth, includeParseEmbeddedRegex, excludeParseEmbeddedRegex, includeExtractEmbeddedRegex, excludeExtractEmbeddedRegex, mimeTypeField,
                outputName);
    }

    private List<String> getListOrString(String path, Config config) {
        try {
            return List.of(config.getString(path));
        } catch (ConfigException.WrongType e) {
            return config.getStringList(path);
        }
    }

    protected Config getDefaultConfig() {
        URL resource = DefaultEmbeddedStrategyConfigConverter.class.getClassLoader().getResource("defaultembeddedstrategy.conf");
        return ConfigFactory.parseURL(resource);
    }

}