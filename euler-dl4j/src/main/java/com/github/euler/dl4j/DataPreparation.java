package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface DataPreparation {

    INDArray prepare(INDArray arr);

}
