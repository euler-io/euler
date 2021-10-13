package com.github.euler.dlj;

import ai.djl.modality.cv.output.DetectedObjects;

public interface DetectedObjectsSerializer {

    Object serialize(DetectedObjects detectedObjects);

}
