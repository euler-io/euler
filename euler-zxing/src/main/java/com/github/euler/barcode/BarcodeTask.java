package com.github.euler.barcode;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.google.zxing.BarcodeFormat;

public class BarcodeTask extends AbstractTask {

    private final StreamFactory sf;
    private final String field;
    private final BarcodeResultSerializer serializer;
    private final BarcodeFormat[] formats;

    public BarcodeTask(String name, StreamFactory sf, String field, BarcodeResultSerializer serializer, BarcodeFormat... formats) {
        super(name);
        this.sf = sf;
        this.field = field;
        this.serializer = serializer;
        this.formats = formats;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new BarcodeItemProcessor(sf, field, serializer, formats);
    }

}
