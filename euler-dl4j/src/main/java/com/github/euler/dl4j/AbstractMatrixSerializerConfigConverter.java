package com.github.euler.dl4j;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMatrixSerializerConfigConverter<T> implements TypeConfigConverter<MatrixSerializer<T>> {

    public static final String TYPE = "matrix-serializer";

    @Override
    public String type() {
        return TYPE;
    }

}
