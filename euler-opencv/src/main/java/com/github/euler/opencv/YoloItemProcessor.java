package com.github.euler.opencv;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class YoloItemProcessor implements ItemProcessor {

    private final Net dnnNet;
    private final StreamFactory sf;
    private final float confThreshold;
    private final MatOfRectSerializer serializer;

    private List<Mat> outputBlobs;
    private List<String> outBlobNames;

    public YoloItemProcessor(Net dnnNet, StreamFactory sf, float confThreshold, MatOfRectSerializer serializer) {
        super();
        this.dnnNet = dnnNet;
        this.sf = sf;
        this.confThreshold = confThreshold;
        this.outputBlobs = new ArrayList<Mat>();
        this.outBlobNames = new ArrayList<String>();
        this.serializer = serializer;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        Mat image;
        try (InputStream in = new BufferedInputStream(sf.openInputStream(item.itemURI, item.ctx))) {
            image = MatUtils.decode(in, Imgcodecs.IMREAD_UNCHANGED);
        }
        outputBlobs.clear();
        outBlobNames.clear();

        Mat blob = Dnn.blobFromImage(image);
        dnnNet.setInput(blob);
        dnnNet.forward(outputBlobs, outBlobNames);

        Map<String, MatOfRect> rectMap = buildMap(blob);
        ProcessingContext.Builder builder = ProcessingContext.builder();

        rectMap.forEach((k, v) -> builder.metadata(k, serializer.serialize(v)));

        return builder.build();
    }

    private Map<String, MatOfRect> buildMap(Mat frame) {
        Map<String, List<Rect>> rectMap = new HashMap<>();
        for (int i = 0; i < outputBlobs.size(); ++i) {
            Mat level = outputBlobs.get(i);
            String label = outBlobNames.get(i);
            for (int j = 0; j < level.rows(); ++j) {
                Mat row = level.row(j);
                Mat scores = row.colRange(5, level.cols());
                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                float confidence = (float) mm.maxVal;
                if (confidence > confThreshold) {
                    int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                    int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                    int width = (int) (row.get(0, 2)[0] * frame.cols());
                    int height = (int) (row.get(0, 3)[0] * frame.rows());
                    int left = centerX - width / 2;
                    int top = centerY - height / 2;

                    List<Rect> matOfRect = rectMap.computeIfAbsent(label, (k) -> new ArrayList<Rect>());
                    matOfRect.add(new Rect(left, top, width, height));
                }
            }
        }
        Map<String, MatOfRect> result = new HashMap<>();
        rectMap.forEach((k, v) -> result.put(k, new MatOfRect(v.stream().toArray(Rect[]::new))));
        return result;
    }

}
