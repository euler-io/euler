package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import com.github.euler.common.StorageStrategy;

public class FileStorageStrategy implements StorageStrategy {

    private final File root;
    private final String suffix;

    public FileStorageStrategy(File root, String suffix) {
        this.root = root;
        this.suffix = suffix;
    }

    @Override
    public URI createFile(URI uri) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        return createFile(baseName, this.suffix);
    }

    @Override
    public URI createFile(URI uri, String suffix) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        return createFile(baseName, suffix);
    }

    public URI createFile(String baseName, String suffix) {
        if (baseName.length() < 2) {
            String uuid = UUID.randomUUID().toString();
            baseName = uuid + baseName;
        }
        File dir = new File(root, baseName.substring(0, 1) + "/" + baseName.substring(1, 2));
        dir.mkdirs();
        String name = baseName + suffix;
        File file = new File(dir, name);
        try {
            file.createNewFile();
            return file.toURI();
        } catch (IOException e) {
            throw new RuntimeException("Could not create file " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public URI createFile(String suffix) {
        return createFile("", suffix);
    }

    @Override
    public URI createFile() {
        return createFile(this.suffix);
    }

    @Override
    public URI createFileWithName(String name) {
        File dir;
        String baseName = FilenameUtils.getBaseName(name);
        if (baseName.length() < 2) {
            baseName = UUID.randomUUID().toString();
        }
        dir = new File(root, baseName.substring(0, 1) + "/" + baseName.substring(1, 2));
        dir.mkdirs();
        File file = new File(dir, name);
        try {
            file.createNewFile();
            return file.toURI();
        } catch (IOException e) {
            throw new RuntimeException("Could not create file " + file.getAbsolutePath(), e);
        }
    }

}
