package com.github.euler.dl4j;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMultiLayerNetworkLoaderConfigConverter implements TypeConfigConverter<MultiLayerNetworkLoader> {

    public static final String TYPE = "multi-layer-network-loader";

    @Override
    public String type() {
        return TYPE;
    }

}
