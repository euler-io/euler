package com.github.euler.tika.metadata;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ValueReplaceMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "value-replace";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String nameRegex = config.getString("name-regex");
        String regex = config.getString("regex");
        String replacement = config.getString("replacement");
        return new ValueReplaceMetadataOperation(nameRegex, regex, replacement);
    }

    protected Config getDefaultConfig() {
        URL resource = ValueReplaceMetadataOperationConfigConverter.class.getClassLoader().getResource("valuereplace.conf");
        return ConfigFactory.parseURL(resource);
    }

}
