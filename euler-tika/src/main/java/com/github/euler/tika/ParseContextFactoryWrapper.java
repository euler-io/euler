package com.github.euler.tika;

import java.util.regex.Pattern;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.parser.ParseContext;

import com.github.euler.core.ProcessingContext;

public class ParseContextFactoryWrapper implements ParseContextFactory {

    private final ParseContextFactory wrapped;
    private final EmbeddedItemListener listener;
    private final boolean parseEmbedded;
    private final Pattern includeExtractEmbeddedPattern;
    private final Pattern excludeExtractEmbeddedPattern;

    public ParseContextFactoryWrapper(ParseContextFactory wrapped, EmbeddedItemListener listener, boolean parseEmbedded, String includeExtractEmbeddedPattern,
            String excludeExtractEmbeddedPattern) {
        super();
        this.wrapped = wrapped;
        this.listener = listener;
        this.parseEmbedded = parseEmbedded;
        this.includeExtractEmbeddedPattern = Pattern.compile(includeExtractEmbeddedPattern);
        this.excludeExtractEmbeddedPattern = Pattern.compile(excludeExtractEmbeddedPattern);
    }

    @Override
    public ParseContext create(ProcessingContext processingContext) {
        ParseContext ctx = wrapped.create(processingContext);
        boolean includeEmbedded = includeEmbedded(processingContext);
        EmbeddedItemExtractor extractor = new EmbeddedItemExtractor(listener, parseEmbedded && includeEmbedded);
        ctx.set(EmbeddedDocumentExtractor.class, extractor);
        return ctx;
    }

    public boolean includeEmbedded(ProcessingContext ctx) {
        Object category = ctx.metadata("category");
        if (category != null) {

            boolean included = this.includeExtractEmbeddedPattern.matcher(category.toString()).matches();
            boolean excluded = this.excludeExtractEmbeddedPattern.matcher(category.toString()).matches();

            return included && !excluded;
        } else {
            return true;
        }
    }

}
