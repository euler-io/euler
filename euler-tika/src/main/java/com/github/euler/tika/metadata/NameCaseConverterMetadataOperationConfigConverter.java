package com.github.euler.tika.metadata;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.tika.metadata.NameCaseConverterMetadataOperation.Case;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class NameCaseConverterMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "name-case-convert";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        Case nameCase = Case.valueOf(config.getString("case").toUpperCase());
        return new NameCaseConverterMetadataOperation(nameCase);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = NameCaseConverterMetadataOperationConfigConverter.class.getClassLoader().getResource("namecaseconverter.conf");
        return ConfigFactory.parseURL(resource);
    }

}
