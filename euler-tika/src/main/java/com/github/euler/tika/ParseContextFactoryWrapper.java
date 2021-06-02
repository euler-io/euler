package com.github.euler.tika;

import java.util.regex.Pattern;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.parser.ParseContext;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.core.ProcessingContext;

public class ParseContextFactoryWrapper implements ParseContextFactory {

    private final ParseContextFactory wrapped;
    private final EmbeddedItemListener listener;
    private final boolean parseEmbedded;
    private final int maxDepth;
    private final Pattern includeExtractEmbeddedPattern;
    private final Pattern excludeExtractEmbeddedPattern;

    public ParseContextFactoryWrapper(ParseContextFactory wrapped, EmbeddedItemListener listener, boolean parseEmbedded, int maxDepth, String includeExtractEmbeddedPattern,
            String excludeExtractEmbeddedPattern) {
        super();
        this.wrapped = wrapped;
        this.listener = listener;
        this.parseEmbedded = parseEmbedded;
        this.maxDepth = maxDepth;
        this.includeExtractEmbeddedPattern = Pattern.compile(includeExtractEmbeddedPattern);
        this.excludeExtractEmbeddedPattern = Pattern.compile(excludeExtractEmbeddedPattern);
    }

    @Override
    public ParseContext create(ProcessingContext processingContext) {
        ParseContext ctx = wrapped.create(processingContext);
        boolean includeEmbedded = includeEmbedded(processingContext);
        boolean aboveMaxDepth = isAboveMaxDepth(processingContext);
        EmbeddedItemExtractor extractor = new EmbeddedItemExtractor(listener, parseEmbedded && includeEmbedded && !aboveMaxDepth);
        ctx.set(EmbeddedDocumentExtractor.class, extractor);
        return ctx;
    }

    private boolean isAboveMaxDepth(ProcessingContext processingContext) {
        int depth = processingContext.context(CommonContext.EXTRACTION_DEPTH, 0);
        return depth > maxDepth;
    }

    public boolean includeEmbedded(ProcessingContext ctx) {
        String metadataName = CommonMetadata.MIME_TYPE;
        Object mimeType = ctx.metadata(metadataName);
        if (mimeType != null) {

            boolean included = this.includeExtractEmbeddedPattern.matcher(mimeType.toString()).matches();
            boolean excluded = this.excludeExtractEmbeddedPattern.matcher(mimeType.toString()).matches();

            return included && !excluded;
        } else {
            return true;
        }
    }

}
