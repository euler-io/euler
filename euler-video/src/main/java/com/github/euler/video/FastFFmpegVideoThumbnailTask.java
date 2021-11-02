package com.github.euler.video;

import com.github.euler.common.StorageStrategy;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;

public class FastFFmpegVideoThumbnailTask extends AbstractTask {

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final int width;
    private final int height;
    private final float position;
    private final String field;
    private final String[] additionalArgs;

    public FastFFmpegVideoThumbnailTask(String name,
            InputFactory inputFactory,
            OutputFactory outputFactory,
            StorageStrategy storageStrategy,
            int width,
            int height,
            float position,
            String field,
            String... additionalArgs) {
        super(name);
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.width = width;
        this.height = height;
        this.position = position;
        this.field = field;
        this.additionalArgs = additionalArgs;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new FastFFmpegVideoThumbnailItemProcessor(inputFactory,
                outputFactory,
                storageStrategy,
                width,
                height,
                position,
                field,
                additionalArgs);
    }

}
