package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface MatrixSerializer<T> {

    T serialize(INDArray matrix);

}
