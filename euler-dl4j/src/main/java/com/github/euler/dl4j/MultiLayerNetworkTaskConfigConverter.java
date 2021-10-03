package com.github.euler.dl4j;

import java.io.IOException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class MultiLayerNetworkTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "multi-layer-network";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typesConfigConverter, TasksConfigConverter tasksConfigConverter) {
        try {
            MultiLayerNetworkLoader networkLoader = typesConfigConverter.convert(AbstractMultiLayerNetworkLoaderConfigConverter.TYPE,
                    config.getValue("model-loader"), ctx);
            MultiLayerNetwork model = networkLoader.load();
            DataLoader loader = typesConfigConverter.convert(AbstractDataLoaderConfigConverter.TYPE, config.getValue("data-loader"), ctx);
            DataPreparation dataPreparation = typesConfigConverter.convert(AbstractDataPreparationConfigConverter.TYPE, config.getValue("data-preparation"), ctx);
            MatrixSerializer<?> serializer = typesConfigConverter.convert(AbstractMatrixSerializerConfigConverter.TYPE, config.getValue("serializer"), ctx);
            String field = config.getString("field");
            return MultiLayerNetworkTask.builder()
                    .setName(getName(config, tasksConfigConverter))
                    .setSf(ctx.getRequired(StreamFactory.class))
                    .setModel(model)
                    .setLoader(loader)
                    .setDataPreparation(dataPreparation)
                    .setMatrixSerializer(serializer)
                    .setField(field)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
