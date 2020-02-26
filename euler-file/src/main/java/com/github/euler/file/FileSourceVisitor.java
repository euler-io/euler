package com.github.euler.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.euler.core.ProcessingContext;

public class FileSourceVisitor extends SimpleFileVisitor<Path> {

    protected final URI uri;
    protected final SourceListener listener;

    public FileSourceVisitor(URI uri, SourceListener listener) {
        super();
        this.uri = uri;
        this.listener = listener;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        notifyFound(dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        notifyFound(file);
        return FileVisitResult.CONTINUE;
    }

    protected void notifyFound(Path path) {
        listener.itemFound(uri, path.normalize().toUri(), ProcessingContext.EMPTY);
    }

}
