package com.github.euler.tika;

import org.apache.tika.parser.AutoDetectParser;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class ParseTask implements Task {

    public static final String PARSED_CONTENT_FILE = ParseTask.class.getName() + ".PARSED_CONTENT_FILE";

    private final String name;
    private final AutoDetectParser parser;
    private final StreamFactory sf;

    public ParseTask(String name, AutoDetectParser parser, StreamFactory sf) {
        this.name = name;
        this.parser = parser;
        this.sf = sf;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return ParseExecution.create(this.parser, this.sf);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return true;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private String name;
        private AutoDetectParser parser;
        private StreamFactory streamFactory;

        private Builder(String name) {
            super();
            this.name = name;
            this.parser = new AutoDetectParser();
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public AutoDetectParser getParser() {
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

        public ParseTask build() {
            return new ParseTask(name, parser, streamFactory);
        }

    }

}
