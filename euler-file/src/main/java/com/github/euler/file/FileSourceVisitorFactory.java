package com.github.euler.file;

import java.net.URI;

public interface FileSourceVisitorFactory {

    public FileSourceVisitor apply(URI uri, SourceListener listener);

}