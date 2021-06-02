package com.github.euler.tika;

import java.util.Objects;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;
import com.github.euler.tika.embedded.DefaultEmbeddedNamingStrategy;
import com.github.euler.tika.metadata.DefaultMetadataParser;
import com.github.euler.tika.metadata.MetadataParser;

import akka.actor.typed.Behavior;

public class ParseTask implements Task {

    private final String name;
    private final Parser parser;
    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;
    private final StorageStrategy embeddedContentStrategy;
    private boolean extractEmbedded;
    private int maxDepth;
    private final String includeExtractEmbeddedPattern;
    private final String excludeExtractEmbeddedPattern;
    private final MetadataParser metadataParser;
    private final ParseContextFactory parseContextFactory;
    private final EmbeddedNamingStrategy embeddedNamingStrategy;

    private ParseTask(String name, Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy, boolean extractEmbedded,
            int maxDepth, String includeExtractEmbeddedPattern, String excludeExtractEmbeddedPattern, MetadataParser metadataParser,
            ParseContextFactory parseContextFactory, EmbeddedNamingStrategy embeddedNamingStrategy) {
        this.name = name;
        this.parser = parser;
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
        this.embeddedContentStrategy = embeddedContentStrategy;
        this.extractEmbedded = extractEmbedded;
        this.maxDepth = maxDepth;
        this.includeExtractEmbeddedPattern = includeExtractEmbeddedPattern;
        this.excludeExtractEmbeddedPattern = excludeExtractEmbeddedPattern;
        this.metadataParser = metadataParser;
        this.parseContextFactory = parseContextFactory;
        this.embeddedNamingStrategy = embeddedNamingStrategy;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return ParseExecution.create(this.parser, this.sf, this.parsedContentStrategy, this.embeddedContentStrategy, this.extractEmbedded, this.maxDepth,
                this.includeExtractEmbeddedPattern,
                this.excludeExtractEmbeddedPattern, this.metadataParser,
                this.parseContextFactory, this.embeddedNamingStrategy);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        boolean isEmpty = sf.isEmpty(msg.itemURI, msg.ctx);
        boolean exists = sf.exists(msg.itemURI, msg.ctx);
        boolean isDirectory = msg.ctx.metadata(CommonMetadata.IS_DIRECTORY, false);
        return exists && !isEmpty && !isDirectory;
    }

    public static Builder builder(String name, StreamFactory streamFactory, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy) {
        return new Builder(name, streamFactory, parsedContentStrategy, embeddedContentStrategy);
    }

    public static class Builder {

        private String name;
        private Parser parser = new AutoDetectParser();
        private StreamFactory streamFactory;
        private StorageStrategy parsedContentStrategy;
        private StorageStrategy embeddedContentStrategy;
        private MetadataParser metadataParser = new DefaultMetadataParser();
        private ParseContextFactory parseContextFactory = new FixedParseContextFactory(new ParseContext());
        private EmbeddedNamingStrategy embeddedNamingStrategy = new DefaultEmbeddedNamingStrategy();
        private boolean extractEmbedded = false;
        private int maxDepth = 10;
        private String includeExtractEmbeddedPattern = ".+";
        private String excludeExtractEmbeddedPattern = "a^";

        private Builder(String name, StreamFactory streamFactory, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy) {
            super();
            this.name = name;
            this.streamFactory = streamFactory;
            this.parsedContentStrategy = parsedContentStrategy;
            this.embeddedContentStrategy = embeddedContentStrategy;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Parser getParser() {
            return parser;
        }

        public Builder setParser(AutoDetectParser parser) {
            this.parser = parser;
            return this;
        }

        public StreamFactory getStreamFactory() {
            return streamFactory;
        }

        public Builder setStreamFactory(StreamFactory streamFactory) {
            this.streamFactory = streamFactory;
            return this;
        }

        public ParseContextFactory getParseContextFactory() {
            return parseContextFactory;
        }

        public Builder setParseContextFactory(ParseContextFactory parseContextFactory) {
            this.parseContextFactory = parseContextFactory;
            return this;
        }

        public Builder setEmbeddedContentStrategy(StorageStrategy embeddedContentStrategy) {
            this.embeddedContentStrategy = embeddedContentStrategy;
            return this;
        }

        public StorageStrategy getEmbeddedContentStrategy() {
            return embeddedContentStrategy;
        }

        public MetadataParser getMetadataParser() {
            return metadataParser;
        }

        public Builder setMetadataParser(MetadataParser metadataParser) {
            this.metadataParser = metadataParser;
            return this;
        }

        public StorageStrategy getParsedContentStrategy() {
            return parsedContentStrategy;
        }

        public Builder setParsedContentStrategy(StorageStrategy parsedContentStrategy) {
            this.parsedContentStrategy = parsedContentStrategy;
            return this;
        }

        public Builder setExtractEmbedded(boolean extractEmbedded) {
            this.extractEmbedded = extractEmbedded;
            return this;
        }

        public Builder setIncludeExtractEmbedded(String includeExtractEmbeddedPattern) {
            this.includeExtractEmbeddedPattern = includeExtractEmbeddedPattern;
            return this;
        }

        public Builder setExcludeExtractEmbedded(String excludeExtractEmbeddedPattern) {
            this.excludeExtractEmbeddedPattern = excludeExtractEmbeddedPattern;
            return this;
        }

        public boolean isExtractEmbedded() {
            return extractEmbedded;
        }

        public int getMaxDepth() {
            return maxDepth;
        }

        public Builder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public String getIncludeExtractEmbeddedPattern() {
            return includeExtractEmbeddedPattern;
        }

        public String getExcludeExtractEmbeddedPattern() {
            return excludeExtractEmbeddedPattern;
        }

        public EmbeddedNamingStrategy getEmbeddedNamingStrategy() {
            return embeddedNamingStrategy;
        }

        public Builder setEmbeddedNamingStrategy(EmbeddedNamingStrategy embeddedNamingStrategy) {
            this.embeddedNamingStrategy = embeddedNamingStrategy;
            return this;
        }

        public ParseTask build() {
            Objects.requireNonNull(name, () -> "name cannot be null");
            Objects.requireNonNull(parser, () -> "parser cannot be null");
            Objects.requireNonNull(streamFactory, () -> "streamFactory cannot be null");
            Objects.requireNonNull(parsedContentStrategy, () -> "parsedContentStrategy cannot be null");
            if (extractEmbedded) {
                Objects.requireNonNull(embeddedContentStrategy, () -> "embeddedContentStrategy cannot be null");
                Objects.requireNonNull(includeExtractEmbeddedPattern, () -> "includeExtractEmbeddedPattern cannot be null");
                Objects.requireNonNull(excludeExtractEmbeddedPattern, () -> "excludeExtractEmbeddedPattern cannot be null");
            }
            Objects.requireNonNull(metadataParser, () -> "metadataParser cannot be null");
            Objects.requireNonNull(parseContextFactory, () -> "parseContextFactory cannot be null");
            Objects.requireNonNull(embeddedNamingStrategy, () -> "embeddedNamingStrategy cannot be null");
            return new ParseTask(name, parser, streamFactory, parsedContentStrategy, embeddedContentStrategy, this.extractEmbedded, this.maxDepth,
                    this.includeExtractEmbeddedPattern,
                    this.excludeExtractEmbeddedPattern, metadataParser, parseContextFactory, embeddedNamingStrategy);
        }

    }

}
