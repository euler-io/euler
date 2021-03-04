package com.github.euler.opencv.operation;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.github.euler.opencv.MatOperation;

public class ChangeCvtOperation implements MatOperation {

    private final int code;

    public ChangeCvtOperation(int code) {
        super();
        this.code = code;
    }

    @Override
    public Mat run(Mat mat) {
        Mat dst = new Mat();
        Imgproc.cvtColor(mat, dst, code);
        return dst;
    }

}
