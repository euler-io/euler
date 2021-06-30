package com.github.euler.tika.metadata;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

public class DefaultMetadataParserConfigConverter extends AbstractMetadataParserConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public MetadataParser convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        String includePattern = config.getString("include-field-regex");
        String excludePattern = config.getString("exclude-field-regex");
        List<MetadataFieldParser> fieldParsers = config.getList("field-parsers").stream()
                .map(c -> convertFieldParser(configContext, typeConfigConverter, c))
                .collect(Collectors.toList());
        return create(config, includePattern, excludePattern, fieldParsers);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected MetadataParser create(Config config, String includePattern, String excludePattern, List<MetadataFieldParser> fieldParsers) {
        return new DefaultMetadataParser(includePattern, excludePattern, fieldParsers);
    }

    protected MetadataFieldParser convertFieldParser(ConfigContext configContext, TypesConfigConverter typeConfigConverter, ConfigValue c) {
        return typeConfigConverter.convert(AbstractMetadataFieldParserConfigConverter.TYPE, c, configContext);
    }

    protected Config getDefaultConfig() {
        URL resource = DefaultMetadataParserConfigConverter.class.getClassLoader().getResource("defaultmetadataparser.conf");
        return ConfigFactory.parseURL(resource);
    }

}
