package com.github.euler.video;

import java.net.URI;
import java.util.Arrays;

import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.ffmpeg.BaseOutput;

public class CustomArgsOutputFactory implements OutputFactory {

    private final OutputFactory wrapped;
    private String[] outputArgs;

    public CustomArgsOutputFactory(OutputFactory wrapped, String... outputArgs) {
        super();
        this.wrapped = wrapped;
        this.outputArgs = outputArgs;
    }

    @Override
    public BaseOutput<?> create(URI itemURI, ProcessingContext ctx) {
        BaseOutput<?> input = wrapped.create(itemURI, ctx);
        Arrays.stream(outputArgs).forEach(a -> input.addArgument(a));
        return input;
    }

}
