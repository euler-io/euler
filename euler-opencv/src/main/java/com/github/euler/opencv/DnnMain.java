package com.github.euler.opencv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVNativeLoader;

//import nu.pattern.OpenCV;

public class DnnMain {

    public static void main(String[] args) throws Exception {
        new OpenCVNativeLoader().init();
        Net dnnNet = Dnn.readNetFromDarknet(args[0], args[1]);

        dnnNet.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        dnnNet.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        System.out.println(dnnNet);

        List<Mat> outputBlobs = new ArrayList<Mat>();
        List<String> outBlobNames = new ArrayList<String>();

        Mat image;
        try (InputStream in = new BufferedInputStream(new FileInputStream(args[2]))) {
            image = MatUtils.decode(in, Imgcodecs.IMREAD_UNCHANGED);
            Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
//            Size scaleSize = new Size(416, 416);
//            Imgproc.resize(image, image, scaleSize, 0, 0, Imgproc.INTER_NEAREST);

            Map<String, MatOfRect> result = detect(image, dnnNet, 0.5f, outputBlobs, outBlobNames);
            result.forEach((k, v) -> System.out.println(k));
        }
    }

    public static Mat decode(InputStream in, int flags) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return Imgcodecs.imdecode(new MatOfByte(bytes), flags);
    }

    private static Map<String, MatOfRect> detect(Mat image, Net dnnNet, float confThreshold, List<Mat> outputBlobs, List<String> outBlobNames) {
        Mat blob = Dnn.blobFromImage(image, 1f / 255f, new Size(608,608), new Scalar(0), true, false);
        System.out.println(blob);
        dnnNet.setInput(blob);
        dnnNet.forward(outputBlobs, outBlobNames);
        return buildMap(blob, confThreshold, outputBlobs, outBlobNames);
    }

    private static Map<String, MatOfRect> buildMap(Mat frame, float confThreshold, List<Mat> outputBlobs, List<String> outBlobNames) {
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
