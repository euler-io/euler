package com.github.euler.opencv;

import java.util.Objects;

import org.opencv.dnn.Net;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

public class DnnNetTask extends AbstractTask {

    private final Net dnnNet;
    private final StreamFactory sf;
    private final float confThreshold;
    private final MatOfRectSerializer serializer;

    private DnnNetTask(String name, Net dnnNet, StreamFactory sf, float confThreshold, MatOfRectSerializer serializer) {
        super(name);
        this.dnnNet = dnnNet;
        this.sf = sf;
        this.confThreshold = confThreshold;
        this.serializer = serializer;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new DnnNetItemProcessor(dnnNet, sf, confThreshold, serializer);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private Net dnnNet;
        private StreamFactory sf;
        private float confThreshold;
        private MatOfRectSerializer serializer;

        private Builder() {
            super();
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Net getDnnNet() {
            return dnnNet;
        }

        public Builder setDnnNet(Net dnnNet) {
            this.dnnNet = dnnNet;
            return this;
        }

        public StreamFactory getSf() {
            return sf;
        }

        public Builder setSf(StreamFactory sf) {
            this.sf = sf;
            return this;
        }

        public float getConfThreshold() {
            return confThreshold;
        }

        public Builder setConfThreshold(float confThreshold) {
            this.confThreshold = confThreshold;
            return this;
        }

        public MatOfRectSerializer getSerializer() {
            return serializer;
        }

        public Builder setSerializer(MatOfRectSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public DnnNetTask build() {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(dnnNet, "dnnNet cannot be null");
            Objects.requireNonNull(sf, "name cannot be null");
            Objects.requireNonNull(confThreshold, "confThreshold cannot be null");
            Objects.requireNonNull(serializer, "serializer cannot be null");
            return new DnnNetTask(name, dnnNet, sf, confThreshold, serializer);
        }

    }

}
