package com.github.euler.dlj;

import java.util.stream.Collectors;

import ai.djl.modality.cv.output.DetectedObjects;

public class ListDetectedObjectsSerializer implements DetectedObjectsSerializer {

    @Override
    public Object serialize(DetectedObjects detectedObjects) {
        return detectedObjects.items().stream()
                .map(o -> o.getClassName()).collect(Collectors.toList());
    }

}
