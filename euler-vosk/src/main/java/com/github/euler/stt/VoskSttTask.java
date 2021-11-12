package com.github.euler.stt;

import org.vosk.Recognizer;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

public class VoskSttTask extends AbstractTask {

    private final StreamFactory sf;
    private final StorageStrategy storageStrategy;
    private final Recognizer recognizer;

    public VoskSttTask(String name, StreamFactory sf, StorageStrategy storageStrategy, Recognizer recognizer) {
        super(name);
        this.sf = sf;
        this.storageStrategy = storageStrategy;
        this.recognizer = recognizer;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new VoskSttItemProcessor(sf,
                storageStrategy,
                recognizer);
    }

}
