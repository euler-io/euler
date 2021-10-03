package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public class SingleLabelMatrixSerializer extends AbstractMatrixSerializer<String> {

    private final String label;
    private final int[] indices;

    public SingleLabelMatrixSerializer(String label, float limitValue, Operator operator, int... indices) {
        super(limitValue, operator);
        this.label = label;
        this.indices = indices;
    }

    public SingleLabelMatrixSerializer(String label, float limitValue, Operator operator) {
        this(label, limitValue, operator, 0);
    }

    @Override
    public String serialize(INDArray matrix) {
        float value = matrix.getFloat(indices);
        return isTrue(value) ? this.label : null;
    }

}
