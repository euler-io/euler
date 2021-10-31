package com.github.euler.video;

import com.github.euler.common.StorageStrategy;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

public class FfmpegVideoPreviewTask extends AbstractTask {

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final int width;
    private final int height;
    private final String[] additionalArgs;

    public FfmpegVideoPreviewTask(String name, InputFactory inputFactory, OutputFactory outputFactory, StorageStrategy storageStrategy, int width,
            int height, String... additionalArgs) {
        super(name);
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.width = width;
        this.height = height;
        this.additionalArgs = additionalArgs;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new FfmpegVideoPreviewItemProcessor(inputFactory,
                outputFactory,
                storageStrategy,
                width,
                height,
                additionalArgs);
    }

}
