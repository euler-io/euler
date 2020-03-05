package com.github.euler.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface StreamFactory {

    InputStream openInputStream(URI uri) throws IOException;

    OutputStream openOutputStream(URI uri) throws IOException;

    boolean exists(URI uri);

    boolean isEmpty(URI uri);

}
