package com.github.euler.video;

import java.net.URI;

import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.ffmpeg.BaseInput;
import com.github.kokorin.jaffree.ffprobe.Input;

public interface InputFactory {

    BaseInput<?> createFFmpeg(URI itemURI, ProcessingContext ctx);

    Input createFFprobe(URI itemURI, ProcessingContext ctx);

}
