package com.github.euler.dl4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;
import com.github.euler.dl4j.JavaRGBImageDataLoader.InterpolationType;

public class MultiLayerNetworkItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final StreamFactory sf;
    private final MultiLayerNetwork model;
    private final DataLoader loader;
    private final DataPreparation dataPreparation;
    private final MatrixSerializer<?> matrixSerializer;
    private final String field;

    public MultiLayerNetworkItemProcessor(StreamFactory sf, MultiLayerNetwork model, DataLoader loader, DataPreparation dataPreparation, MatrixSerializer<?> matrixSerializer,
            String field) {
        super();
        this.sf = sf;
        this.model = model;
        this.loader = loader;
        this.dataPreparation = dataPreparation;
        this.matrixSerializer = matrixSerializer;
        this.field = field;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        try (InputStream in = sf.openInputStream(item.itemURI, item.ctx)) {
            Object classification = apply(in);
            return ProcessingContext.builder()
                    .metadata(field, classification)
                    .setAction(Action.MERGE)
                    .build();
        } catch (Exception e) {
            LOGGER.warn("An error ocurred while creating video preview for " + item.itemURI, e);
            return ProcessingContext.EMPTY;
        }
    }

    private Object apply(InputStream in) throws IOException {
        INDArray arr = loader.load(in);
        arr = dataPreparation.prepare(arr);
        INDArray output = model.output(arr);
        return matrixSerializer.serialize(output);
    }

}
