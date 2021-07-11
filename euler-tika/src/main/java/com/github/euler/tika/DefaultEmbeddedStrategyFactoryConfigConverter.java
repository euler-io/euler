package com.github.euler.tika;

import java.net.URL;
import java.util.List;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class DefaultEmbeddedStrategyFactoryConfigConverter extends AbstractEmbeddedStrategyFactoryConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public EmbeddedStrategyFactory convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        int maxDepth = config.getInt("max-depth");
        List<String> includeParseEmbeddedRegex = getListOrString("parse.include-regex", config);
        List<String> excludeParseEmbeddedRegex = getListOrString("parse.exclude-regex", config);
        List<String> includeExtractEmbeddedRegex = getListOrString("extract.include-regex", config);
        List<String> excludeExtractEmbeddedRegex = getListOrString("extract.exclude-regex", config);
        String mimeTypeField = config.getString("mime-type-field");
        boolean outputName = config.getBoolean("output-name");
        return new DefaultEmbeddedStrategyFactory(maxDepth, includeParseEmbeddedRegex, excludeParseEmbeddedRegex, includeExtractEmbeddedRegex, excludeExtractEmbeddedRegex,
                mimeTypeField,
                outputName);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    private List<String> getListOrString(String path, Config config) {
        try {
            return List.of(config.getString(path));
        } catch (ConfigException.WrongType e) {
            return config.getStringList(path);
        }
    }

    protected Config getDefaultConfig() {
        URL resource = DefaultEmbeddedStrategyFactoryConfigConverter.class.getClassLoader().getResource("defaultembeddedstrategy.conf");
        return ConfigFactory.parseURL(resource);
    }

}
