package com.github.euler.preview;

import java.net.URI;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class PreviewTask extends AbstractTask {

    private final EulerPreview preview;
    private final PreviewContext previewContext;
    private final String formatName;
    private final StreamFactory sf;
    private final StorageStrategy storageStrategy;

    public PreviewTask(String name, EulerPreview preview, PreviewContext previewContext, String formatName, StreamFactory sf, StorageStrategy storageStrategy) {
        super(name);
        this.preview = preview;
        this.previewContext = previewContext;
        this.formatName = formatName;
        this.sf = sf;
        this.storageStrategy = storageStrategy;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new PreviewItemProcessor(preview, previewContext, formatName, sf, storageStrategy);
    }

    @Override
    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        String mime = (String) ctx.metadata(CommonMetadata.MIME_TYPE, ctx.context(CommonMetadata.MIME_TYPE));
        if (mime != null) {
            try {
                MediaType mediaType = MediaType.parse(mime);
                return preview.isSupported(mediaType);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            return false;
        }
    }

}
