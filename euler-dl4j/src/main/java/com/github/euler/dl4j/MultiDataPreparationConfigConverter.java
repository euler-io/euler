package com.github.euler.dl4j;

import java.util.List;
import java.util.stream.Collectors;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class MultiDataPreparationConfigConverter extends AbstractDataPreparationConfigConverter {

    @Override
    public String configType() {
        return "multi";
    }

    @Override
    public DataPreparation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        List<DataPreparation> preparations = config.getList("preparations").stream()
                .map(c -> (DataPreparation) typeConfigConverter.convert(AbstractDataPreparationConfigConverter.TYPE, c, configContext))
                .collect(Collectors.toList());
        return new MultiDataPreparation(preparations);
    }

}
