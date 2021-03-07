package com.github.euler.preview;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class PreviewItemProcessor implements ItemProcessor {

    public static final String PREVIEW_METADATA = "preview";

    private final EulerPreview preview;
    private final PreviewContext ctx;
    private final String formatName;
    private final ScalrConfig resizeConfig;
    private final StreamFactory streamFactory;
    private final StorageStrategy storageStrategy;

    public PreviewItemProcessor(EulerPreview preview, PreviewContext ctx, String formatName, ScalrConfig resizeConfig, StreamFactory streamFactory,
            StorageStrategy storageStrategy) {
        super();
        this.preview = preview;
        this.ctx = ctx;
        this.formatName = formatName;
        this.resizeConfig = resizeConfig;
        this.streamFactory = streamFactory;
        this.storageStrategy = storageStrategy;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        MediaType type = getMime(item);
        URI outFile = storageStrategy.createFile("." + formatName.toLowerCase());

        InputStream in = null;
        OutputStream out = null;
        try {
            in = streamFactory.openInputStream(item.itemURI, item.ctx);
            out = streamFactory.openOutputStream(outFile, item.ctx);
            ResizePreviewHandler handler = new ResizePreviewHandler(resizeConfig, new OutputStreamPreviewHandler(out, formatName), false);
            preview.generate(ctx, type, in, handler);
            ProcessingContext.Builder builder = ProcessingContext.builder();
            builder.metadata(PREVIEW_METADATA, outFile.toString());
            return builder.build();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private MediaType getMime(Item item) {
        String mime = (String) item.ctx.metadata(CommonMetadata.MIME_TYPE, item.ctx.context(CommonMetadata.MIME_TYPE));
        return MediaType.parse(mime);
    }

}
