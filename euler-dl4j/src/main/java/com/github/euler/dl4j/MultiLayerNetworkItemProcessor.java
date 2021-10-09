package com.github.euler.dl4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;
import com.github.euler.dl4j.JavaRGBImageDataLoader.InterpolationType;

public class MultiLayerNetworkItemProcessor implements ItemProcessor {

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
        }
    }

    private Object apply(InputStream in) throws IOException {
        INDArray arr = loader.load(in);
        arr = dataPreparation.prepare(arr);
        INDArray output = model.output(arr);
        return matrixSerializer.serialize(output);
    }

    public static void main(String[] args) throws Exception {
        MultiLayerNetwork kerasModel = KerasModelImport.importKerasSequentialModelAndWeights("/home/dell/Documents/whatssapp_screenshot.h5", false);

        int size = 64;
        JavaRGBImageDataLoader loader = new JavaRGBImageDataLoader(size, size, InterpolationType.NEAREST);
        DataPreparation dataPreparation = new MultiDataPreparation(
                new ImageScalerDataPreparation(new ImagePreProcessingScaler(0, 1)),
                new PermuteNHWCDataPreparation(),
                new ImageChannelSelectionDataPreparation(size, size, 1, 2, 3));
        List<String> imgs = List.of(
                "/home/dell/Pictures/test/Screenshot from 2020-09-01 16-01-14.png",
                "/home/dell/Pictures/test/ef01d4f553f011074f85155487ab2962.jpg",
                "/home/dell/Pictures/test/testocr.png");

        MatrixSerializer<?> serializer = new FloatMatrixSerializer();

        MultiLayerNetworkItemProcessor itemProcessor = new MultiLayerNetworkItemProcessor(null, kerasModel, loader, dataPreparation, serializer, "");
        for (String img : imgs) {
            try (InputStream in = new FileInputStream(img)) {
                Object result = itemProcessor.apply(in);
                System.out.println(result);
            }
        }

    }

}
