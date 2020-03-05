package com.github.euler.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import org.junit.Test;

import com.github.euler.common.StorageStrategy;

public class FileStorageStrategyTest {

    @Test
    public void createFileWithSuffix() throws IOException {
        String suffix = ".txt";
        URI uri = Files.createTempFile("file", ".tmp").toUri();

        File root = Files.createTempDirectory("root").toFile();

        StorageStrategy strategy = new FileStorageStrategy(root, suffix);
        URI created = strategy.createFile(uri);
        assertTrue(FileUtils.toFile(created).exists());
        assertTrue(created.toString().endsWith(suffix));
        assertTrue(FileUtils.toFile(created).getAbsolutePath().startsWith(root.getAbsolutePath()));
    }

}
