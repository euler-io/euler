package com.github.euler.opencv;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractMatOfRectSerializerConfigConverter implements TypeConfigConverter<MatOfRectSerializer> {

    public static final String MAT_OF_RECT_SERIALIZER = "mat-of-rect-serializer";

    @Override
    public String type() {
        return MAT_OF_RECT_SERIALIZER;
    }

}
