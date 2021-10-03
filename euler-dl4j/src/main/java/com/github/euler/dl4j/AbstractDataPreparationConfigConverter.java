package com.github.euler.dl4j;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractDataPreparationConfigConverter implements TypeConfigConverter<DataPreparation> {

    public static final String TYPE = "data-preparation";

    @Override
    public String type() {
        return TYPE;
    }

}
