package com.github.euler.video;

import java.net.URI;

import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.ffmpeg.BaseInput;
import com.github.kokorin.jaffree.ffmpeg.BaseOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

public class URLInputOutputFactory implements OutputFactory, InputFactory {

    @Override
    public BaseInput<?> createFFmpeg(URI itemURI, ProcessingContext ctx) {
        return UrlInput.fromUrl(itemURI.toString());
    }

    @Override
    public com.github.kokorin.jaffree.ffprobe.Input createFFprobe(URI itemURI, ProcessingContext ctx) {
        return com.github.kokorin.jaffree.ffprobe.UrlInput.fromUrl(itemURI.toString());
    }

    @Override
    public BaseOutput<?> create(URI itemURI, ProcessingContext ctx) {
        return UrlOutput.toUrl(itemURI.toString());
    }

}
