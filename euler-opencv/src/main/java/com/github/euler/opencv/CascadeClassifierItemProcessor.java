package com.github.euler.opencv;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.opencv.operation.VoidOperation;

public class CascadeClassifierItemProcessor implements ItemProcessor {

    private final String classifierName;
    private final CascadeClassifier classifier;
    private final StreamFactory sf;
    private final MatOperation matOperation;
    private final MatOfRectSerializer serializer;

    public CascadeClassifierItemProcessor(String classifierName, CascadeClassifier classifier, StreamFactory sf, MatOperation matOperation, MatOfRectSerializer serializer) {
        super();
        this.classifierName = classifierName;
        this.classifier = classifier;
        this.sf = sf;
        this.matOperation = matOperation;
        this.serializer = serializer;
    }

    public CascadeClassifierItemProcessor(String classifierName, CascadeClassifier classifier, StreamFactory sf) {
        this(classifierName, classifier, sf, new VoidOperation(), new BooleanMatOfRectSerializer());
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        Mat image;
        try (InputStream in = new BufferedInputStream(sf.openInputStream(item.itemURI, item.ctx))) {
            image = MatUtils.decode(in, Imgcodecs.IMREAD_UNCHANGED);
        }
        image = matOperation.run(image);
        MatOfRect rects = new MatOfRect();
        classifier.detectMultiScale(image, rects);
        Object result = serializer.serialize(rects);
        return ProcessingContext.builder().metadata(classifierName, result).build();
    }

}
