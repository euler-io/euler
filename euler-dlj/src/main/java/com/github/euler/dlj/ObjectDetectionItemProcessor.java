package com.github.euler.dlj;

import java.io.IOException;
import java.io.InputStream;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

public class ObjectDetectionItemProcessor implements ItemProcessor {

    private final StreamFactory sf;
    private final ZooModel<Image, DetectedObjects> model;
    private final DetectedObjectsSerializer serializer;
    private final String field;

    private Predictor<Image, DetectedObjects> predictor = null;

    public ObjectDetectionItemProcessor(StreamFactory sf, ZooModel<Image, DetectedObjects> model, DetectedObjectsSerializer serializer, String field) {
        super();
        this.sf = sf;
        this.model = model;
        this.serializer = serializer;
        this.field = field;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        try (InputStream in = sf.openInputStream(item.itemURI, item.ctx)) {
            Image img = ImageFactory.getInstance().fromInputStream(in);

            DetectedObjects detectedObjects = getPredictor().predict(img);

            return ProcessingContext.builder()
                    .metadata(field, serializer.serialize(detectedObjects))
                    .setAction(Action.MERGE)
                    .build();
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    protected Predictor<Image, DetectedObjects> getPredictor() {
        if (predictor == null) {
            predictor = model.newPredictor();
        }
        return predictor;
    }

}
