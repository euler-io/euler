package com.github.euler.barcode;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class DefaultBarcodeResultSerializerConfigConverter extends AbstractBarcodeResultSerializerConfigConverter {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public BarcodeResultSerializer convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new DefaultBarcodeResultSerializer();
    }

}
