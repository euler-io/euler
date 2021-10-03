package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class ImageScalerDataPreparation implements DataPreparation {

    private final ImagePreProcessingScaler scaler;

    public ImageScalerDataPreparation(ImagePreProcessingScaler scaler) {
        super();
        this.scaler = scaler;
    }

    @Override
    public INDArray prepare(INDArray arr) {
        scaler.transform(arr);
        return arr;
    }

}
