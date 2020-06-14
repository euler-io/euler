package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

public interface Source {

    void scan(URI uri, SourceListener listener) throws IOException;

}
