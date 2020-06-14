package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import com.github.euler.core.CancellableSource;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.SourceCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class FileSource extends CancellableSource {

    public static Behavior<SourceCommand> create(int maxItemsPerYield) {
        return Behaviors.setup((context) -> new FileSource(context, maxItemsPerYield));
    }

    public static Behavior<SourceCommand> create() {
        return create(100);
    }

    private final int maxItemsPerYield;
    private int itemsFound;
    private Iterator<File> fileIterator;

    protected FileSource(ActorContext<SourceCommand> context, int maxItemsPerYield) {
        super(context);
        this.maxItemsPerYield = maxItemsPerYield;
    }

    @Override
    protected void prepareScan(URI uri) {
        Path path = FileUtils.toPath(uri);
        if (path.toFile().isDirectory()) {
            fileIterator = new FileTreeIterator(path.toFile());
        } else {
            fileIterator = Arrays.asList(path.toFile()).iterator();
        }
    }

    @Override
    protected boolean doScan(URI uri) throws IOException {
        while (fileIterator.hasNext() && itemsFound <= maxItemsPerYield) {
            itemsFound++;
            File found = fileIterator.next();
            notifyItemFound(uri, found.toURI(), ProcessingContext.EMPTY);
        }
        if (fileIterator.hasNext()) {
            itemsFound = 0;
            yield();
        }
        return !fileIterator.hasNext();
    }

}
