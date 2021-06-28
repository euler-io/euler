package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import com.github.euler.common.CommonMetadata;
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

    public DefaultEmbeddedStrategy(int maxDepth, List<String> includeParseEmbeddedRegex, List<String> excludeParseEmbeddedRegex,
            List<String> includeExtractEmbeddedRegex, List<String> excludeExtractEmbeddedRegex,
            String mimeTypeField, boolean outputName) {
        super();
        this.maxDepth = maxDepth;
        this.includeParseEmbeddedPatterns = toPattern(includeParseEmbeddedRegex);
        this.excludeParseEmbeddedPatterns = toPattern(excludeParseEmbeddedRegex);
        this.includeExtractEmbeddedPatterns = toPattern(includeExtractEmbeddedRegex);
        this.excludeExtractEmbeddedPatterns = toPattern(excludeExtractEmbeddedRegex);
        this.mimeTypeField = mimeTypeField;
        this.outputName = outputName;
    }

    private List<Pattern> toPattern(List<String> regex) {
        return regex.stream()
                .map(r -> Pattern.compile(r))
                .collect(Collectors.toList());
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int maxDepth = 10;
        private List<String> includeParseEmbeddedRegex = List.of(".+");
        private List<String> excludeParseEmbeddedRegex = List.of("a^");
        private List<String> includeExtractEmbeddedRegex = List.of("a^");
        private List<String> excludeExtractEmbeddedRegex = List.of("a^");
        private String mimeTypeField = CommonMetadata.MIME_TYPE;
        private boolean outputName = false;

        private Builder() {
            super();
        }

        public int getMaxDepth() {
            return maxDepth;
        }

        public Builder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public List<String> getIncludeParseEmbeddedRegex() {
            return includeParseEmbeddedRegex;
        }

        public Builder setIncludeParseEmbeddedRegex(List<String> includeParseEmbeddedRegex) {
            this.includeParseEmbeddedRegex = includeParseEmbeddedRegex;
            return this;
        }

        public Builder setIncludeParseEmbeddedRegex(String includeParseEmbeddedRegex) {
            this.includeParseEmbeddedRegex = List.of(includeParseEmbeddedRegex);
            return this;
        }

        public List<String> getExcludeParseEmbeddedRegex() {
            return excludeParseEmbeddedRegex;
        }

        public Builder setExcludeParseEmbeddedRegex(List<String> excludeParseEmbeddedRegex) {
            this.excludeParseEmbeddedRegex = excludeParseEmbeddedRegex;
            return this;
        }

        public Builder setExcludeParseEmbeddedRegex(String excludeParseEmbeddedRegex) {
            this.excludeParseEmbeddedRegex = List.of(excludeParseEmbeddedRegex);
            return this;
        }

        public List<String> getIncludeExtractEmbeddedRegex() {
            return includeExtractEmbeddedRegex;
        }

        public Builder setIncludeExtractEmbeddedRegex(List<String> includeExtractEmbeddedRegex) {
            this.includeExtractEmbeddedRegex = includeExtractEmbeddedRegex;
            return this;
        }

        public Builder setIncludeExtractEmbeddedRegex(String includeExtractEmbeddedRegex) {
            this.includeExtractEmbeddedRegex = List.of(includeExtractEmbeddedRegex);
            return this;
        }

        public List<String> getExcludeExtractEmbeddedRegex() {
            return excludeExtractEmbeddedRegex;
        }

        public Builder setExcludeExtractEmbeddedRegex(List<String> excludeExtractEmbeddedRegex) {
            this.excludeExtractEmbeddedRegex = excludeExtractEmbeddedRegex;
            return this;
        }

        public Builder setExcludeExtractEmbeddedRegex(String excludeExtractEmbeddedRegex) {
            this.excludeExtractEmbeddedRegex = List.of(excludeExtractEmbeddedRegex);
            return this;
        }

        public String getMimeTypeField() {
            return mimeTypeField;
        }

        public Builder setMimeTypeField(String mimeTypeField) {
            this.mimeTypeField = mimeTypeField;
            return this;
        }

        public boolean isOutputName() {
            return outputName;
        }

        public Builder setOutputName(boolean outputName) {
            this.outputName = outputName;
            return this;
        }

        public DefaultEmbeddedStrategy build() {
            return new DefaultEmbeddedStrategy(maxDepth, includeParseEmbeddedRegex, excludeParseEmbeddedRegex, includeExtractEmbeddedRegex, excludeExtractEmbeddedRegex,
                    mimeTypeField, outputName);
        }

    }

}
