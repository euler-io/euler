package com.github.euler.tika.metadata;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class StringMetadataFieldParserConfigConverter extends AbstractMetadataFieldParserConfigConverter {

    @Override
    public String configType() {
        return "string";
    }

    @Override
    public MetadataFieldParser convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new StringMetadataFieldParser();
    }

}
