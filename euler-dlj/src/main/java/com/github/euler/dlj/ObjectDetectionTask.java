package com.github.euler.dlj;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;

public class ObjectDetectionTask extends AbstractTask {

    private final StreamFactory sf;
    private final ZooModel<Image, DetectedObjects> model;
    private final DetectedObjectsSerializer serializer;
    private final String field;

    public ObjectDetectionTask(String name, StreamFactory sf, ZooModel<Image, DetectedObjects> model, DetectedObjectsSerializer serializer, String field) {
        super(name);
        this.sf = sf;
        this.model = model;
        this.serializer = serializer;
        this.field = field;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new ObjectDetectionItemProcessor(sf, model, serializer, field);
    }

}
