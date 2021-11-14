package com.github.euler.stt;

import org.vosk.Recognizer;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.FieldType;
import com.github.euler.core.ItemProcessor;

public class VoskSttTask extends AbstractTask {

    private final StreamFactory sf;
    private final StorageStrategy storageStrategy;
    private final Recognizer recognizer;
    private final String inputField;
    private final FieldType inputFieldType;

    public VoskSttTask(String name,
            StreamFactory sf,
            StorageStrategy storageStrategy,
            Recognizer recognizer,
            String inputField,
            FieldType inputFieldType) {
        super(name);
        this.sf = sf;
        this.storageStrategy = storageStrategy;
        this.recognizer = recognizer;
        this.inputField = inputField;
        this.inputFieldType = inputFieldType;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new VoskSttItemProcessor(sf,
                storageStrategy,
                recognizer,
                inputField,
                inputFieldType);
    }

}
