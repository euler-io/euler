package com.github.euler.tika;

import java.util.Objects;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class ParseTask implements Task {

    public static final String PARSED_CONTENT_FILE = ParseTask.class.getName() + ".PARSED_CONTENT_FILE";

    private final String name;
    private final Parser parser;
    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;
    private final MetadataParser metadataParser;
    private final ParseContextFactory parseContextFactory;

    public ParseTask(String name, Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, MetadataParser metadataParser, ParseContextFactory parseContextFactory) {
        this.name = name;
        this.parser = parser;
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
        this.metadataParser = metadataParser;
        this.parseContextFactory = parseContextFactory;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return ParseExecution.create(this.parser, this.sf, this.parsedContentStrategy, this.metadataParser, this.parseContextFactory);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return sf.exists(msg.itemURI) && !sf.isEmpty(msg.itemURI);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private String name;
        private Parser parser;
        private StreamFactory streamFactory;
        private StorageStrategy parsedContentStrategy;
        private MetadataParser metadataParser;
        private ParseContextFactory parseContextFactory;

        private Builder(String name) {
            super();
            this.name = name;
            this.parser = new AutoDetectParser();
            this.metadataParser = new DefaultMetadataParser();
            this.parseContextFactory = new FixedParseContextFactory(new ParseContext());
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

        public ParseTask build() {
            Objects.requireNonNull(name, () -> "name cannont be null");
            Objects.requireNonNull(parser, () -> "parser cannont be null");
            Objects.requireNonNull(streamFactory, () -> "streamFactory cannont be null");
            Objects.requireNonNull(parsedContentStrategy, () -> "parsedContentStrategy cannont be null");
            Objects.requireNonNull(metadataParser, () -> "metadataParser cannont be null");
            Objects.requireNonNull(parseContextFactory, () -> "parseContextFactory cannont be null");
            return new ParseTask(name, parser, streamFactory, parsedContentStrategy, metadataParser, parseContextFactory);
        }

    }

}
