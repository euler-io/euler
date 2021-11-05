package com.github.euler.video;

import java.net.URI;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.IncludeExcludePattern;
import com.github.euler.common.StorageStrategy;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class FfmpegVideoEncodeTask extends AbstractTask {

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final String field;
    private final IncludeExcludePattern mimeTypePattern;
    private final String[] additionalArgs;

    public FfmpegVideoEncodeTask(String name,
            InputFactory inputFactory,
            OutputFactory outputFactory,
            StorageStrategy storageStrategy,
            String field,
            IncludeExcludePattern mimeTypePattern,
            String... additionalArgs) {
        super(name);
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.field = field;
        this.mimeTypePattern = mimeTypePattern;
        this.additionalArgs = additionalArgs;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new FfmpegVideoEncodeItemProcessor(inputFactory,
                outputFactory,
                storageStrategy,
                field,
                additionalArgs);
    }

    @Override
    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        return isFormatAcceptable(ctx);
    }

    private boolean isFormatAcceptable(ProcessingContext ctx) {
        String mimeType = (String) ctx.metadata(CommonMetadata.MIME_TYPE);
        if (mimeType != null) {
            return mimeTypePattern.isIncluded(mimeType);
        }
        return true;
    }

}
