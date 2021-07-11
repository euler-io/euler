package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.DelegatingParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.github.euler.common.CommonContext;
import com.github.euler.core.ProcessingContext;

public class DefaultEmbeddedStrategy extends EmbeddedStrategy {

    private static final Parser DELEGATING_PARSER = new DelegatingParser();

    private static final String LINE_BREAK = "\n";

    private final int maxDepth;
    private final List<Pattern> includeParseEmbeddedPatterns;
    private final List<Pattern> excludeParseEmbeddedPatterns;
    private final List<Pattern> includeExtractEmbeddedPatterns;
    private final List<Pattern> excludeExtractEmbeddedPatterns;
    private final String mimeTypeField;
    private final boolean outputName;

    public DefaultEmbeddedStrategy(int maxDepth, List<Pattern> includeParseEmbeddedPatterns, List<Pattern> excludeParseEmbeddedPatterns,
            List<Pattern> includeExtractEmbeddedPatterns, List<Pattern> excludeExtractEmbeddedPatterns,
            String mimeTypeField, boolean outputName) {
        super();
        this.maxDepth = maxDepth;
        this.includeParseEmbeddedPatterns = includeParseEmbeddedPatterns;
        this.excludeParseEmbeddedPatterns = excludeParseEmbeddedPatterns;
        this.includeExtractEmbeddedPatterns = includeExtractEmbeddedPatterns;
        this.excludeExtractEmbeddedPatterns = excludeExtractEmbeddedPatterns;
        this.mimeTypeField = mimeTypeField;
        this.outputName = outputName;
    }

    @Override
    public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws SAXException, IOException {
        String resourceName = metadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
        if (outputName && resourceName != null) {
            handler.characters(resourceName.toCharArray(), 0, resourceName.length());
            if (outputHtml) {
                handler.startElement(XHTMLContentHandler.XHTML, "br", "br", new AttributesImpl());
                handler.endElement(XHTMLContentHandler.XHTML, "br", "br");
            } else {
                handler.characters(LINE_BREAK.toCharArray(), 0, LINE_BREAK.length());
            }
        }

        if (shouldExtractEmbedded(metadata)) {
            notifyNewExtractedEmbedded(stream, metadata);
        } else {
            try {
                DELEGATING_PARSER.parse(
                        stream,
                        new EmbeddedContentHandler(new BodyContentHandler(handler)),
                        metadata, getParseContext());
            } catch (TikaException e) {
                throw new IOException(e);
            }
        }

    }

    protected boolean shouldExtractEmbedded(Metadata metadata) {
        String mimeType = metadata.get(Metadata.CONTENT_TYPE);
        if (mimeType == null) {
            mimeType = "application/unknown";
        }
        return isIncluded(includeExtractEmbeddedPatterns, excludeExtractEmbeddedPatterns, mimeType);
    }

    @Override
    protected boolean shouldParseEmbedded(ProcessingContext ctx) {
        String mimeType = ctx.metadata(mimeTypeField, null);
        boolean included = isIncluded(includeParseEmbeddedPatterns, excludeParseEmbeddedPatterns, mimeType);
        return included && !isAboveMaxDepth(ctx);
    }

    private boolean isAboveMaxDepth(ProcessingContext processingContext) {
        int depth = processingContext.context(CommonContext.EXTRACTION_DEPTH, 0);
        return depth > maxDepth;
    }

    protected boolean isIncluded(List<Pattern> includedPatterns, List<Pattern> excludedPatterns, String value) {
        if (value != null) {

            boolean included = anyMatches(includedPatterns, value);
            boolean excluded = anyMatches(excludedPatterns, value);

            return included && !excluded;
        } else {
            return false;
        }
    }

    private boolean anyMatches(List<Pattern> patterns, String value) {
        return patterns.stream()
                .anyMatch(p -> p.matcher(value).matches());
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public List<Pattern> getIncludeParseEmbeddedPatterns() {
        return includeParseEmbeddedPatterns;
    }

    public List<Pattern> getExcludeParseEmbeddedPatterns() {
        return excludeParseEmbeddedPatterns;
    }

    public List<Pattern> getIncludeExtractEmbeddedPatterns() {
        return includeExtractEmbeddedPatterns;
    }

    public List<Pattern> getExcludeExtractEmbeddedPatterns() {
        return excludeExtractEmbeddedPatterns;
    }

    public String getMimeTypeField() {
        return mimeTypeField;
    }

    public boolean isOutputName() {
        return outputName;
    }

}
