package com.github.euler.dlj;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;

public class YoloV5Main {

    public static void main(String[] args) throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(640, 640));
        pipeline.add(new ToTensor());
        YoloV5Translator translator = YoloV5Translator.builder()
                .setPipeline(pipeline)
                .optSynsetArtifactName("/media/dell/storage/coco.names")
                .optThreshold(0.2f)
                .build();
        Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optModelPath(Paths.get("/home/dell/projetos/yolov5/yolov5s.torchscript.pt"))
                .optTranslator(translator)
                .optProgress(new ProgressBar())
                .build();

        try (ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
            try (InputStream in = new FileInputStream(new File("/tmp/b4m1b7kiyaawrna.jpg"))) {
                Image img = ImageFactory.getInstance().fromInputStream(in);
                Predictor<Image, DetectedObjects> predictor = model.newPredictor();
                DetectedObjects detectedObjects = predictor.predict(img);
                System.out.println(detectedObjects);
            }
        }
    }

}
