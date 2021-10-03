package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public class FloatMatrixSerializer implements MatrixSerializer<Float> {

    private int[] indices;

    public FloatMatrixSerializer(int... indices) {
        super();
        this.indices = indices;
    }

    public FloatMatrixSerializer() {
        this(0);
    }

    @Override
    public Float serialize(INDArray matrix) {
        return matrix.getFloat(indices);
    }

}
