package com.github.euler.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FileTreeIterator implements Iterator<File> {

    private File root;

    private List<Iterator<File>> dirIteratorStack = null;
    private Iterator<File> fileIterator = null;

    private boolean rootReturned = false;

    public FileTreeIterator(File root) {
        this.root = root;
        this.dirIteratorStack = new ArrayList<>();
        init();
    }

    private void init() {
        this.fileIterator = Arrays.stream(root.listFiles(fileOnly)).iterator();
        pushStack(root);
    }

    @Override
    public boolean hasNext() {
        return hasRoot() || hasNextFile() || hasNextDir();
    }

    private boolean hasRoot() {
        return !this.rootReturned;
    }

    private boolean hasNextFile() {
        return fileIterator != null && fileIterator.hasNext();
    }

    private boolean hasNextDir() {
        while (dirIteratorStack.size() > 0) {
            Iterator<File> topIterator = getTopIterator();
            if (topIterator.hasNext()) {
                return true;
            } else {
                popStack();
            }
        }
        return false;
    }

    @Override
    public File next() {
        if (hasRoot()) {
            return root();
        } else if (hasNextFile()) {
            return nextFile();
        } else if (hasNextDir()) {
            return nextDir();
        } else {
            throw new NoSuchElementException();
        }
    }

    private File root() {
        this.rootReturned = true;
        return root;
    }

    private File nextFile() {
        File file = fileIterator.next();
        if (!fileIterator.hasNext()) {
            fileIterator = null;
        }
        return file;
    }

    private File nextDir() {
        Iterator<File> dirIterator = getTopIterator();

        File dir = dirIterator.next();
        this.fileIterator = Arrays.stream(dir.listFiles(fileOnly)).iterator();
        pushStack(dir);
        return dir;
    }

    private void pushStack(File dir) {
        dirIteratorStack.add(Arrays.stream(dir.listFiles(dirOnly)).iterator());
    }

    private void popStack() {
        dirIteratorStack.remove(dirIteratorStack.size() - 1);
    }

    private Iterator<File> getTopIterator() {
        return dirIteratorStack.get(dirIteratorStack.size() - 1);
    }

    private final FileFilter fileOnly = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isFile();
        }
    };

    private final FileFilter dirOnly = new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.isDirectory();
        }
    };

}
