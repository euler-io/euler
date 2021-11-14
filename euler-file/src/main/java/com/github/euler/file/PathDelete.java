package com.github.euler.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathDelete implements Closeable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final String path;
    private final boolean failSafe;

    public PathDelete(String path, boolean failSafe) {
        super();
        this.path = path;
        this.failSafe = failSafe;
    }

    @Override
    public void close() throws IOException {
        File file = new File(path);
        if (failSafe) {
            safeDelete(file);
        } else {
            file.delete();
        }
    }

    private void safeDelete(File file) {
        try {
            file.delete();
        } catch (Exception e) {
            LOGGER.warn("Error deleting " + file.getAbsolutePath(), e);
        }
    }

}
