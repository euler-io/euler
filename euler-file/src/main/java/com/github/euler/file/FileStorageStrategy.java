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
        String uuid = UUID.randomUUID().toString();
        File dir = new File(root, uuid.substring(0, 1) + "/" + uuid.substring(1, 2));
        dir.mkdirs();
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        String name = uuid + "-" + baseName + suffix;
        File file = new File(dir, name);
        try {
            file.createNewFile();
            return file.toURI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
