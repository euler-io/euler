package com.github.euler.tika;

import org.apache.tika.parser.ParseContext;

import com.github.euler.core.ProcessingContext;

public class FixedParseContextFactory implements ParseContextFactory {

    private final ParseContext context;

    public FixedParseContextFactory(ParseContext context) {
        super();
        this.context = context;
    }

    @Override
    public ParseContext create(ProcessingContext ctx) {
        return context;
    }

}
