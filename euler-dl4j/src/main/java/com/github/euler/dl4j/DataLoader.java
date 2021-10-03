package com.github.euler.dl4j;

import java.io.IOException;
import java.io.InputStream;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface DataLoader {

    INDArray load(InputStream in) throws IOException;

}
