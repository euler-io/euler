package com.github.euler.dlj;

import java.util.stream.Collectors;

import ai.djl.modality.cv.output.DetectedObjects;

public class MaxProbDetectedObjectsSerializer implements DetectedObjectsSerializer {

    @Override
    public Object serialize(DetectedObjects detectedObjects) {
        return detectedObjects.items().stream()
                .collect(Collectors.toMap(
                        o -> o.getClassName(),
                        o -> o.getProbability(),
                        (p1, p2) -> Math.max(p1, p2)));
    }

}
