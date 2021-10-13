package com.github.euler.dlj;

import java.io.IOException;
import java.nio.file.Paths;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;

public class YoloV5ModelLoader implements ZooModelLoader<Image, DetectedObjects> {

    private final String classesPath;
    private final String modelPath;
    private final float confidenceThreshold;
    private final int width;
    private final int height;

    public YoloV5ModelLoader(String classesPath, String modelPath, float confidenceThreshold, int width, int height) {
        super();
        this.classesPath = classesPath;
        this.modelPath = modelPath;
        this.confidenceThreshold = confidenceThreshold;
        this.width = width;
        this.height = height;
    }

    @Override
    public ZooModel<Image, DetectedObjects> load() throws IOException, ModelNotFoundException, MalformedModelException {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(width, height));
        pipeline.add(new ToTensor());
        YoloV5Translator translator = YoloV5Translator.builder()
                .setPipeline(pipeline)
                .optSynsetArtifactName(classesPath)
                .optThreshold(confidenceThreshold)
                .build();
        Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optModelPath(Paths.get(modelPath))
                .optTranslator(translator)
                .optProgress(new ProgressBar()).build();
        return criteria.loadModel();
    }

}
