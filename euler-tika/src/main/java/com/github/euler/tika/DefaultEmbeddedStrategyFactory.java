package com.github.euler.tika;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.euler.common.CommonMetadata;

public class DefaultEmbeddedStrategyFactory implements EmbeddedStrategyFactory {

    private final int maxDepth;
    private final List<Pattern> includeParseEmbeddedPatterns;
    private final List<Pattern> excludeParseEmbeddedPatterns;
    private final List<Pattern> includeExtractEmbeddedPatterns;
    private final List<Pattern> excludeExtractEmbeddedPatterns;
    private final String mimeTypeField;
    private final boolean outputName;

    public DefaultEmbeddedStrategyFactory(int maxDepth, List<String> includeParseEmbeddedRegex, List<String> excludeParseEmbeddedRegex,
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
        return Collections.unmodifiableList(regex.stream()
                .map(r -> Pattern.compile(r))
                .collect(Collectors.toList()));
    }

    @Override
    public EmbeddedStrategy newEmbeddedStrategy(EmbeddedItemListener listener) {
        DefaultEmbeddedStrategy strategy = new DefaultEmbeddedStrategy(maxDepth, includeParseEmbeddedPatterns, excludeParseEmbeddedPatterns, includeExtractEmbeddedPatterns,
                excludeExtractEmbeddedPatterns, mimeTypeField,
                outputName);
        strategy.setListener(listener);
        return strategy;
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

        public DefaultEmbeddedStrategyFactory build() {
            return new DefaultEmbeddedStrategyFactory(maxDepth, includeParseEmbeddedRegex, excludeParseEmbeddedRegex, includeExtractEmbeddedRegex, excludeExtractEmbeddedRegex,
                    mimeTypeField, outputName);
        }

    }

}
