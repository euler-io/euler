package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class KerasSequentialModelAndWeightsMultiLayerNetworkLoaderConfigConverter extends AbstractMultiLayerNetworkLoaderConfigConverter {

    @Override
    public String configType() {
        return "keras-sequential-model-and-weights";
    }

    @Override
    public MultiLayerNetworkLoader convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        String path = config.getString("path");
        return new KerasSequentialModelAndWeightsMultiLayerNetworkLoader(path);
    }

}
