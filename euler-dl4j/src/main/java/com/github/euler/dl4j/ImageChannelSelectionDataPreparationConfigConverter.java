package com.github.euler.dl4j;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class ImageChannelSelectionDataPreparationConfigConverter extends AbstractDataPreparationConfigConverter {

    @Override
    public String configType() {
        return "image-channel-selection";
    }

    @Override
    public DataPreparation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        int width = config.getInt("width");
        int height = config.getInt("height");
        int[] channels = config.getIntList("channels").stream().mapToInt(i -> i).toArray();
        return new ImageChannelSelectionDataPreparation(width, height, channels);
    }

}
