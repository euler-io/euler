package com.github.euler.barcode;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractBarcodeResultSerializerConfigConverter implements TypeConfigConverter<BarcodeResultSerializer> {

    public static final String TYPE = "barcode-result-serializer";

    @Override
    public String type() {
        return TYPE;
    }

}
