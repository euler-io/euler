package com.github.euler.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.ProcessingContext;

public class FileStreamFactory implements StreamFactory {

    @Override
    public InputStream openInputStream(URI uri, ProcessingContext ctx) throws IOException {
        return new FileInputStream(FileUtils.toFile(uri));
    }

    @Override
    public OutputStream openOutputStream(URI uri, ProcessingContext ctx) throws IOException {
        return new FileOutputStream(FileUtils.toFile(uri));
    }

    @Override
    public boolean exists(URI uri, ProcessingContext ctx) {
        return FileUtils.toFile(uri).exists();
    }

    @Override
    public boolean isEmpty(URI uri, ProcessingContext ctx) {
        return FileUtils.toFile(uri).length() == 0;
    }

}
