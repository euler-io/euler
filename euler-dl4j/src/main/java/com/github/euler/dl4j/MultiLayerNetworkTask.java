package com.github.euler.dl4j;

import java.util.Objects;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

public class MultiLayerNetworkTask extends AbstractTask {

    private final StreamFactory sf;
    private final MultiLayerNetwork model;
    private final DataLoader loader;
    private final DataPreparation dataPreparation;
    private final MatrixSerializer<?> matrixSerializer;
    private final String field;

    private MultiLayerNetworkTask(String name, StreamFactory sf, MultiLayerNetwork model, DataLoader loader, DataPreparation dataPreparation, MatrixSerializer<?> matrixSerializer,
            String field) {
        super(name);
        this.sf = sf;
        this.model = model;
        this.loader = loader;
        this.dataPreparation = dataPreparation;
        this.matrixSerializer = matrixSerializer;
        this.field = field;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new MultiLayerNetworkItemProcessor(sf, model, loader, dataPreparation, matrixSerializer, field);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private StreamFactory sf;
        private MultiLayerNetwork model;
        private DataLoader loader;
        private DataPreparation dataPreparation;
        private MatrixSerializer<?> matrixSerializer;
        private String field;

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public StreamFactory getSf() {
            return sf;
        }

        public Builder setSf(StreamFactory sf) {
            this.sf = sf;
            return this;
        }

        public MultiLayerNetwork getModel() {
            return model;
        }

        public Builder setModel(MultiLayerNetwork model) {
            this.model = model;
            ;
            return this;
        }

        public DataLoader getLoader() {
            return loader;
        }

        public Builder setLoader(DataLoader loader) {
            this.loader = loader;
            ;
            return this;
        }

        public DataPreparation getDataPreparation() {
            return dataPreparation;
        }

        public Builder setDataPreparation(DataPreparation dataPreparation) {
            this.dataPreparation = dataPreparation;
            ;
            return this;
        }

        public MatrixSerializer<?> getMatrixSerializer() {
            return matrixSerializer;
        }

        public Builder setMatrixSerializer(MatrixSerializer<?> matrixSerializer) {
            this.matrixSerializer = matrixSerializer;
            ;
            return this;
        }

        public String getField() {
            return field;
        }

        public Builder setField(String field) {
            this.field = field;
            ;
            return this;
        }

        public MultiLayerNetworkTask build() {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(sf, "sf cannot be null");
            Objects.requireNonNull(model, "model cannot be null");
            Objects.requireNonNull(loader, "loader cannot be null");
            Objects.requireNonNull(dataPreparation, "dataPreparation cannot be null");
            Objects.requireNonNull(matrixSerializer, "matrixSerializer cannot be null");
            Objects.requireNonNull(field, "field cannot be null");
            return new MultiLayerNetworkTask(name, sf, model, loader, dataPreparation, matrixSerializer, field);
        }

    }

}
