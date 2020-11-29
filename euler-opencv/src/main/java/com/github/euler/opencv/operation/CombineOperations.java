package com.github.euler.opencv.operation;

import org.opencv.core.Mat;

import com.github.euler.opencv.MatOperation;

public class CombineOperations implements MatOperation {

    private final MatOperation[] operations;

    public CombineOperations(MatOperation... operations) {
        super();
        this.operations = operations;
    }

    @Override
    public Mat run(Mat mat) {
        for (MatOperation operation : operations) {
            mat = operation.run(mat);
        }
        return mat;
    }

}
