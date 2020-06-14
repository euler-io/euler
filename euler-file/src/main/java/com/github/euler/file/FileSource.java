package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import com.github.euler.core.AbstractPausableSource;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.SourceListener;

public class FileSource extends AbstractPausableSource {

    private final int maxItemsPerYield;
    private int itemsFound;
    private Iterator<File> fileIterator;
    private URI uri;

    public FileSource(int maxItemsPerYield) {
        this.maxItemsPerYield = maxItemsPerYield;
    }

    public FileSource() {
        this.maxItemsPerYield = 100;
    }

    @Override
    public void prepareScan(URI uri) {
        this.uri = uri;
        Path path = FileUtils.toPath(uri);
        if (path.toFile().isDirectory()) {
            fileIterator = new FileTreeIterator(path.toFile());
        } else {
            fileIterator = Arrays.asList(path.toFile()).iterator();
        }
    }

    @Override
    public boolean doScan(SourceListener listener) throws IOException {
        while (fileIterator.hasNext() && itemsFound <= maxItemsPerYield) {
            itemsFound++;
            File found = fileIterator.next();
            listener.notifyItemFound(uri, found.toURI(), ProcessingContext.EMPTY);
        }
        if (fileIterator.hasNext()) {
            itemsFound = 0;
        }
        return !fileIterator.hasNext();
    }

    @Override
    public void finishScan() {

    }

}
