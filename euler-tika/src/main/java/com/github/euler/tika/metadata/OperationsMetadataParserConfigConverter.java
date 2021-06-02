package com.github.euler.tika.metadata;

import java.util.List;
import java.util.stream.Collectors;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public class OperationsMetadataParserConfigConverter extends AbstractMetadataParserConfigConverter {

    @Override
    public String configType() {
        return "operations";
    }

    @Override
    public MetadataParser convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        MetadataParser parser = typeConfigConverter.convert(AbstractMetadataParserConfigConverter.TYPE, config.getValue("parser"), configContext);
        List<MetadataOperation> operations = config.getList("operations").stream()
                .map(c -> convertOperation(configContext, typeConfigConverter, c))
                .collect(Collectors.toList());
        return new OperationsMetadataParser(parser, operations);
    }

    private MetadataOperation convertOperation(ConfigContext configContext, TypesConfigConverter typeConfigConverter, ConfigValue c) {
        return typeConfigConverter.convert(AbstractMetadataOperationConfigConverter.TYPE, c, configContext);
    }

}
