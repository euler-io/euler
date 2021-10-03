package com.github.euler.dl4j;

import java.io.IOException;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class KerasSequentialModelAndWeightsMultiLayerNetworkLoader implements MultiLayerNetworkLoader {

    private final String path;

    public KerasSequentialModelAndWeightsMultiLayerNetworkLoader(String path) {
        super();
        this.path = path;
    }

    @Override
    public MultiLayerNetwork load() throws IOException {
        try {
            return KerasModelImport.importKerasSequentialModelAndWeights(path, false);
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
