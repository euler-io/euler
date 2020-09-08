package com.github.euler.tika;

import java.io.IOException;

import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class MimeTypeItemProcessor implements ItemProcessor {

    private final StreamFactory sf;
    private final Detector detector;

    public MimeTypeItemProcessor(StreamFactory sf, Detector detector) {
        this.sf = sf;
        this.detector = detector;
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
            try (TikaInputStream tikaInputStream = TikaInputStream.get(sf.openInputStream(item.itemURI))) {
                MediaType type = detector.detect(tikaInputStream, metadata);
                type = type.getBaseType();
                mimeType = type.getType() + "/" + type.getSubtype();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ProcessingContext.builder()
                .metadata(CommonMetadata.MIME_TYPE, mimeType)
                .build();
    }

}
