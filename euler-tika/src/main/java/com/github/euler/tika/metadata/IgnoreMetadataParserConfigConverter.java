package com.github.euler.tika.metadata;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class IgnoreMetadataParserConfigConverter extends AbstractMetadataParserConfigConverter {

    @Override
    public String configType() {
        return "ignore";
    }

    @Override
    public MetadataParser convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new IgnoreMetadataParser();
    }

}
