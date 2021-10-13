package com.github.euler.dlj;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractDetectedObjectsSerializerConfigConverter implements TypeConfigConverter<DetectedObjectsSerializer> {

    public static final String TYPE = "detected-objects-serializer";

    @Override
    public String type() {
        return TYPE;
    }

}
