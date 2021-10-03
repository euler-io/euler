package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public class BooleanMatrixSerializer extends AbstractMatrixSerializer<Boolean> {

    private final int[] indices;

    public BooleanMatrixSerializer(float limitValue, Operator operator, int... indices) {
        super(limitValue, operator);
        this.indices = indices;
    }

    public BooleanMatrixSerializer(float limitValue, Operator operator) {
        this(limitValue, operator, 0);
    }

    @Override
    public Boolean serialize(INDArray matrix) {
        float value = matrix.getFloat(indices);
        return isTrue(value);
    }

}
