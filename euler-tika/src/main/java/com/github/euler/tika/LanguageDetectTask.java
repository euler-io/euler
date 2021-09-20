package com.github.euler.tika;

import java.net.URI;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class LanguageDetectTask extends AbstractTask {

    private final StreamFactory sf;
    private final long maxBytes;
    private final int maxLangs;
    private final String field;

    public LanguageDetectTask(String name, StreamFactory sf, long maxBytes, int maxLangs, String field) {
        super(name);
        this.sf = sf;
        this.maxBytes = maxBytes;
        this.maxLangs = maxLangs;
        this.field = field;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new LanguageDetectItemProcessor(sf, maxBytes, maxLangs, field);
    }

    @Override
    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        URI parsedUri = ctx.context(CommonContext.PARSED_CONTENT_FILE, null);
        Boolean isDir = ctx.metadata(CommonMetadata.IS_DIRECTORY, false);
        return parsedUri != null && sf.exists(parsedUri, ctx) && !sf.isEmpty(parsedUri, ctx) && !isDir;
    }

}
