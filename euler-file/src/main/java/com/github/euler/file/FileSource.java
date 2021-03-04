package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.github.euler.core.AbstractPausableSource;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.SourceListener;

public class FileSource extends AbstractPausableSource {

    private static final int DEFAULT_MAX_ITEMS_PER_YIELD = 100;
    private static final Pattern DEFAULT_REGEX = Pattern.compile(".+");
    private static final boolean DEFAULT_NOTIFY_DIRECTORIES = true;

    private final int maxItemsPerYield;
    private final Pattern regex;
    private final boolean notifyDirectories;

    private int itemsFound;
    private Iterator<File> fileIterator;
    private URI uri;

    public FileSource(int maxItemsPerYield) {
        this(maxItemsPerYield, DEFAULT_REGEX, DEFAULT_NOTIFY_DIRECTORIES);
    }

    public FileSource() {
        this(DEFAULT_MAX_ITEMS_PER_YIELD, DEFAULT_REGEX, DEFAULT_NOTIFY_DIRECTORIES);
    }

    public FileSource(Pattern regex) {
        this(DEFAULT_MAX_ITEMS_PER_YIELD, regex, DEFAULT_NOTIFY_DIRECTORIES);
    }

    public FileSource(int maxItemsPerYield, Pattern regex, boolean notifyDirectories) {
        this.maxItemsPerYield = maxItemsPerYield;
        this.regex = regex;
        this.notifyDirectories = notifyDirectories;
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
            File found = fileIterator.next();
            if ((found.isDirectory() && notifyDirectories) || (found.isFile() && regex.matcher(found.getAbsolutePath()).matches())) {
                itemsFound++;
                listener.notifyItemFound(uri, found.toURI(), ProcessingContext.EMPTY);
            }
        }
        if (fileIterator.hasNext()) {
            itemsFound = 0;
        }
        return !fileIterator.hasNext();
    }

    @Override
    public void finishScan() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int maxItemsPerYield = DEFAULT_MAX_ITEMS_PER_YIELD;
        private Pattern regex = DEFAULT_REGEX;
        private boolean notifyDirectories = DEFAULT_NOTIFY_DIRECTORIES;

        private Builder() {
            super();
        }

        public int getMaxItemsPerYield() {
            return maxItemsPerYield;
        }

        public Builder setMaxItemsPerYield(int maxItemsPerYield) {
            this.maxItemsPerYield = maxItemsPerYield;
            return this;
        }

        public Pattern getRegex() {
            return regex;
        }

        public Builder setRegex(Pattern regex) {
            this.regex = regex;
            return this;
        }

        public boolean isNotifyDirectories() {
            return notifyDirectories;
        }

        public Builder setNotifyDirectories(boolean notifyDirectories) {
            this.notifyDirectories = notifyDirectories;
            return this;
        }

        public FileSource build() {
            return new FileSource(maxItemsPerYield, regex, notifyDirectories);
        }

    }

}
