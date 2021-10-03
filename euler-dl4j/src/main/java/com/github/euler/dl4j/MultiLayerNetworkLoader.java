package com.github.euler.dl4j;

import java.io.IOException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public interface MultiLayerNetworkLoader {

    MultiLayerNetwork load() throws IOException;

}
