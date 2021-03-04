package com.github.euler.opencv;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMatOperationTypeConfigConverter implements TypeConfigConverter<MatOperation> {

    public static final String MAT_OPERATION = "mat-operation";

    @Override
    public String type() {
        return MAT_OPERATION;
    }

}
