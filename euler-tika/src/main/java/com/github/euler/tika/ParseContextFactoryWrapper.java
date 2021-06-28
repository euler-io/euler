package com.github.euler.tika;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.parser.ParseContext;

import com.github.euler.core.ProcessingContext;

public class ParseContextFactoryWrapper implements ParseContextFactory {

    private final ParseContextFactory wrapped;
    private EmbeddedStrategy embeddedStrategy;

    public ParseContextFactoryWrapper(ParseContextFactory wrapped, EmbeddedStrategy embeddedStrategy) {
        super();
        this.wrapped = wrapped;
        this.embeddedStrategy = embeddedStrategy;
    }

    @Override
    public ParseContext create(ProcessingContext processingContext) {
        ParseContext ctx = wrapped.create(processingContext);
        if (embeddedStrategy.shouldParseEmbedded(processingContext)) {
            embeddedStrategy.setParseContext(ctx);
            ctx.set(EmbeddedDocumentExtractor.class, embeddedStrategy);
        }
        return ctx;
    }

}
