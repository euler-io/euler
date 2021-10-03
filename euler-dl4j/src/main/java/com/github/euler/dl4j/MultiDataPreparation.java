package com.github.euler.dl4j;

import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

public class MultiDataPreparation implements DataPreparation {

    private final List<DataPreparation> preparations;

    public MultiDataPreparation(List<DataPreparation> preparations) {
        super();
        this.preparations = preparations;
    }

    public MultiDataPreparation(DataPreparation... preparations) {
        super();
        this.preparations = List.of(preparations);
    }

    @Override
    public INDArray prepare(INDArray arr) {
        for (DataPreparation dataPreparation : preparations) {
            arr = dataPreparation.prepare(arr);
        }
        return arr;
    }

}
