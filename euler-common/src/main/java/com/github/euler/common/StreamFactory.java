package com.github.euler.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.github.euler.core.ProcessingContext;

public interface StreamFactory {

    InputStream openInputStream(URI uri, ProcessingContext ctx) throws IOException;

    OutputStream openOutputStream(URI uri, ProcessingContext ctx) throws IOException;

    boolean exists(URI uri, ProcessingContext ctx);

    boolean isEmpty(URI uri, ProcessingContext ctx);

}
