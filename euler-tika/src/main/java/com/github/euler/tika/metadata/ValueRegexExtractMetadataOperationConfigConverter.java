package com.github.euler.tika.metadata;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ValueRegexExtractMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "value-regex-extract";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String nameRegex = config.getString("name-regex");
        String regex = config.getString("regex");
        return new ValueRegexExtractMetadataOperation(nameRegex, regex);
    }

    protected Config getDefaultConfig() {
        URL resource = ValueRegexExtractMetadataOperationConfigConverter.class.getClassLoader().getResource("extract-regex.conf");
        return ConfigFactory.parseURL(resource);
    }

}
