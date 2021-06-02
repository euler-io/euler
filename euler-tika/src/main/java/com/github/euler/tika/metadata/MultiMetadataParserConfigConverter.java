package com.github.euler.tika.metadata;

import java.util.List;
import java.util.stream.Collectors;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class MultiMetadataParserConfigConverter extends AbstractMetadataParserConfigConverter {

    @Override
    public String configType() {
        return "multi";
    }

    @Override
    public MetadataParser convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        List<MetadataParser> parsers = config.getList("parsers").stream()
                .map(c -> convertParser(configContext, typeConfigConverter, c))
                .collect(Collectors.toList());
        return new MultiMetadataParser(parsers);
    }

    private MetadataParser convertParser(ConfigContext configContext, TypesConfigConverter typeConfigConverter, ConfigValue c) {
        return typeConfigConverter.convert(AbstractMetadataParserConfigConverter.TYPE, c, configContext);
    }

}
