package com.github.euler.tika.metadata;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.metadata.NameCaseConverterMetadataOperation.Case;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class NameCaseConverterMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "name-case-convert";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        Case nameCase = Case.valueOf(config.getString("case").toUpperCase());
        return new NameCaseConverterMetadataOperation(nameCase);
    }

    protected Config getDefaultConfig() {
        URL resource = NameCaseConverterMetadataOperationConfigConverter.class.getClassLoader().getResource("namecaseconverter.conf");
        return ConfigFactory.parseURL(resource);
    }

}
