package com.github.euler.tika;

import java.io.InputStream;
import java.util.Objects;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;

import com.github.euler.core.ProcessingContext;

public abstract class EmbeddedStrategy implements EmbeddedDocumentExtractor {

    private EmbeddedItemListener listener = null;
    private ParseContext parseContext;

    protected final ParseContext getParseContext() {
        return parseContext;
    }

    protected final void setParseContext(ParseContext parseContext) {
        this.parseContext = parseContext;
    }

    public final EmbeddedItemListener getListener() {
        return listener;
    }

    public final void setListener(EmbeddedItemListener listener) {
        this.listener = listener;
    }

    protected final void notifyNewExtractedEmbedded(InputStream in, Metadata metadata) {
        Objects.requireNonNull(listener, () -> EmbeddedItemListener.class.getName() + " not provided.");
        listener.newEmbedded(in, metadata);
    }

    @Override
    public final boolean shouldParseEmbedded(Metadata metadata) {
        return true;
    }

    protected abstract boolean shouldParseEmbedded(ProcessingContext processingContext);

}
