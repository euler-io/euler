package com.github.euler.tika;

import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class MimeTypeItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String field;
    private final StreamFactory sf;
    private final Detector detector;

    public MimeTypeItemProcessor(String field, StreamFactory sf, Detector detector) {
        this.field = field;
        this.sf = sf;
        this.detector = detector;
    }

    public MimeTypeItemProcessor(StreamFactory sf, Detector detector) {
        this(CommonMetadata.MIME_TYPE, sf, detector);
    }

    @Override
    public ProcessingContext process(Item item) {
        String mimeType = null;

        Boolean isDirectory = (Boolean) item.ctx.metadata(CommonMetadata.IS_DIRECTORY);
        if (isDirectory != null && isDirectory) {
            mimeType = "text/directory";
        } else {
            Metadata metadata = new Metadata();
            if (item.ctx.metadata().containsKey(CommonMetadata.NAME)) {
                metadata.set(Metadata.RESOURCE_NAME_KEY, item.ctx.metadata(CommonMetadata.NAME).toString());
            }
            try (TikaInputStream tikaInputStream = TikaInputStream.get(sf.openInputStream(item.itemURI, item.ctx))) {
                MediaType type = detector.detect(tikaInputStream, metadata);
                type = type.getBaseType();
                mimeType = type.getType() + "/" + type.getSubtype();
            } catch (Throwable e) {
                LOGGER.warn("An error occurred while detecting mime type for {}: {}", item.itemURI, e.getMessage());
            }
        }
        return ProcessingContext.builder()
                .metadata(this.field, mimeType)
                .build();
    }

}
