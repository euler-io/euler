package com.github.euler.tika.metadata;

import java.net.URL;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class ValueIntConverterMetadataOperationConfigConverter extends AbstractMetadataOperationConfigConverter {

    @Override
    public String configType() {
        return "int-converter";
    }

    @Override
    public MetadataOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        String nameRegex = config.getString("name-regex");
        boolean failSafe = config.getBoolean("fail-safe");
        int defaultValueOnError = config.getInt("default-value-on-error");
        return new ValueIntConverterMetadataOperation(nameRegex, failSafe, defaultValueOnError);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = NameCaseConverterMetadataOperationConfigConverter.class.getClassLoader().getResource("intconverter.conf");
        return ConfigFactory.parseURL(resource);
    }

}
