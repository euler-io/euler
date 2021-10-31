package com.github.euler.video;

import java.net.URI;

import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.ffmpeg.BaseOutput;

public interface OutputFactory {

    BaseOutput<?> create(URI itemURI, ProcessingContext ctx);

}
