package com.github.euler.tika;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import com.github.euler.core.ProcessingContext;

public class ParseContextFactoryWrapper implements ParseContextFactory {

    private final Parser parser;
    private final ParseContextFactory wrapped;
    private final EmbeddedStrategy embeddedStrategy;

    public ParseContextFactoryWrapper(Parser parser, ParseContextFactory wrapped, EmbeddedStrategy embeddedStrategy) {
        super();
        this.parser = parser;
        this.wrapped = wrapped;
        this.embeddedStrategy = embeddedStrategy;
    }

    @Override
    public ParseContext create(ProcessingContext processingContext) {
        ParseContext ctx = wrapped.create(processingContext);
        ctx.set(Parser.class, parser);
        if (embeddedStrategy.shouldParseEmbedded(processingContext)) {
            embeddedStrategy.setParseContext(ctx);
            ctx.set(EmbeddedDocumentExtractor.class, embeddedStrategy);
        }
        return ctx;
    }

}
