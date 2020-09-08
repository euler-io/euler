package com.github.euler.tika;

import java.net.URI;

import org.apache.tika.detect.Detector;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class MimeTypeDetectTask extends AbstractTask {

    private final StreamFactory sf;
    private final Detector detector;

    public MimeTypeDetectTask(String name, StreamFactory sf, Detector detector) {
        super(name);
        this.sf = sf;
        this.detector = detector;
    }

    @Override
    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        return !ctx.metadata().containsKey(CommonMetadata.MIME_TYPE);
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new MimeTypeItemProcessor(sf, detector);
    }

}
