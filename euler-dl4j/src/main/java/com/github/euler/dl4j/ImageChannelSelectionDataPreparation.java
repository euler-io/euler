package com.github.euler.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ImageChannelSelectionDataPreparation implements DataPreparation {

    private final int width;
    private final int height;
    private final int[] channels;

    public ImageChannelSelectionDataPreparation(int width, int height, int... channels) {
        super();
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    @Override
    public INDArray prepare(INDArray arr) {
        float[][][][] output = new float[1][width][height][channels.length];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int c = 0; c < channels.length; c++) {
                    int channel = channels[c];
                    output[0][x][y][c] = arr.getFloat(0, x, y, channel);
                }
            }
        }
        return Nd4j.create(output);
    }

}
