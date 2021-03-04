package com.github.euler.opencv.operation;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.github.euler.opencv.MatOperation;

public class EqualizeHistOperation implements MatOperation {

    @Override
    public Mat run(Mat mat) {
        Imgproc.equalizeHist(mat, mat);
        return mat;
    }

}
