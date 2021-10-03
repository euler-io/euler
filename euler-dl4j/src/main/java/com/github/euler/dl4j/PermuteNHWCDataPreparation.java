package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public class PermuteNHWCDataPreparation implements DataPreparation {

    private final static int[] REARRANGE = { 0, 2, 3, 1 };

    @Override
    public INDArray prepare(INDArray arr) {
        return arr.permute(REARRANGE);
    }

}
