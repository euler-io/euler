package com.github.euler.tika;

import org.apache.tika.parser.ParseContext;

import com.github.euler.core.ProcessingContext;

public interface ParseContextFactory {

    ParseContext create(ProcessingContext ctx);

}
