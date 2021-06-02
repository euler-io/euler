package com.github.euler.tika;

import java.net.URI;

import com.github.euler.common.CommonContext;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.AbstractTask;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class StripHTMLTask extends AbstractTask {

    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;

    public StripHTMLTask(String name, StreamFactory sf, StorageStrategy parsedContentStrategy) {
        super(name);
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return new StripHTMLItemProcessor(sf, parsedContentStrategy);
    }

    @Override
    protected boolean accept(URI uri, URI itemURI, ProcessingContext ctx) {
        return ctx.context().containsKey(CommonContext.PARSED_CONTENT_FILE);
    }

}
