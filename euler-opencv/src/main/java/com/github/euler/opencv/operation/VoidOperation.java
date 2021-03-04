package com.github.euler.opencv.operation;

import org.opencv.core.Mat;

import com.github.euler.opencv.MatOperation;

public class VoidOperation implements MatOperation {

    @Override
    public Mat run(Mat mat) {
        return mat;
    }

}
