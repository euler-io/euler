package com.github.euler.dl4j;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class DL4JExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new KerasSequentialModelAndWeightsMultiLayerNetworkLoaderConfigConverter(),
                new JavaRGBImageDataLoaderConfigConverter(),
                new ImageChannelSelectionDataPreparationConfigConverter(),
                new ImageScalerDataPreparationConfigConverter(),
                new MultiDataPreparationConfigConverter(),
                new PermuteNHWCDataPreparationConfigConverter(),
                new BooleanMatrixSerializerConfigConverter(),
                new FloatMatrixSerializerConfigConverter(),
                new LabelsMatrixSerializerConfigConverter(),
                new SingleLabelMatrixSerializerConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(new MultiLayerNetworkTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "DL4J Extension";
    }

}
