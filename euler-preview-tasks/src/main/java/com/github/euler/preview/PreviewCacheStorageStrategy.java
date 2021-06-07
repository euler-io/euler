package com.github.euler.preview;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.euler.common.StorageStrategy;
import com.github.euler.file.FileUtils;

public class PreviewCacheStorageStrategy implements StorageStrategy {

    private final File root;
    private final String suffix;

    private final int width;
    private final int height;

    public PreviewCacheStorageStrategy(File root, String suffix, int width, int height) {
        super();
        this.root = root;
        this.suffix = suffix;
        this.width = width;
        this.height = height;
    }

    @Override
    public URI createFile(URI uri) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        String hash = DigestUtils.md5Hex(FileUtils.toFile(uri).getAbsolutePath()).toLowerCase();
        return createFile(baseName, hash, this.suffix);
    }

    @Override
    public URI createFile(String suffix) {
        String baseName = UUID.randomUUID().toString();
        String hash = DigestUtils.md5Hex(baseName).toLowerCase();
        return createFile(baseName, hash, suffix);
    }

    @Override
    public URI createFile(URI uri, String suffix) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        String hash = DigestUtils.md5Hex(FileUtils.toFile(uri).getAbsolutePath()).toLowerCase();
        return createFile(baseName, hash, suffix);
    }

    public URI createFile(String baseName, String hash, String suffix) {
        String fileName = baseName + "-" + height + "x" + width + "-" + hash + suffix;
        File dir = new File(root, fileName);
        dir.mkdirs();
        return dir.toURI();
    }

    @Override
    public URI createFile() {
        return createFile(this.suffix);
    }

    @Override
    public URI createFileWithName(String name) {
        File dir = new File(root, name);
        dir.mkdirs();
        return dir.toURI();
    }

}
