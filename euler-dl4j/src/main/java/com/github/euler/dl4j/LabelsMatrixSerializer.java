package com.github.euler.dl4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.nd4j.linalg.api.ndarray.INDArray;

public class LabelsMatrixSerializer extends AbstractMatrixSerializer<List<String>> {

    private final List<String> labels;
    private final int rowIndex;

    public LabelsMatrixSerializer(List<String> labels, int rowIndex, float limitValue, Operator operator) {
        super(limitValue, operator);
        this.labels = labels;
        this.rowIndex = rowIndex;
    }

    public LabelsMatrixSerializer(List<String> labels, float limitValue, Operator operator) {
        super(limitValue, operator);
        this.labels = labels;
        this.rowIndex = 0;
    }

    @Override
    public List<String> serialize(INDArray matrix) {
        INDArray row = matrix.getRow(rowIndex);
        return IntStream.range(0, (int) row.length())
                .filter(i -> isTrue(row.getFloat(i)))
                .mapToObj(i -> labels.get(i))
                .collect(Collectors.toList());
    }

}
