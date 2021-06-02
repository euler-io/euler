package com.github.euler.tika.metadata;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class NameReplaceMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "name-replace";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String regex = config.getString("regex");
        String replacement = config.getString("replacement");
        return new NameReplaceMetadataOperation(regex, replacement);
    }

}
