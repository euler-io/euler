package com.github.euler.video;

import java.net.URI;
import java.util.Arrays;

import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.ffmpeg.BaseInput;
import com.github.kokorin.jaffree.ffprobe.Input;

public class CustomArgsInputFactory implements InputFactory {

    private final InputFactory wrapped;
    private String[] inputArgs;

    public CustomArgsInputFactory(InputFactory wrapped, String... inputArgs) {
        super();
        this.wrapped = wrapped;
        this.inputArgs = inputArgs;
    }

    @Override
    public BaseInput<?> createFFmpeg(URI itemURI, ProcessingContext ctx) {
        BaseInput<?> input = wrapped.createFFmpeg(itemURI, ctx);
        Arrays.stream(inputArgs).forEach(a -> input.addArgument(a));
        return input;
    }

    @Override
    public Input createFFprobe(URI itemURI, ProcessingContext ctx) {
        return wrapped.createFFprobe(itemURI, ctx);
    }

}
