package com.github.euler.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import org.junit.Test;

public class FileUtilsTest {

    @Test
    public void testGetRelativePath() throws Exception {
        File root = Files.createTempDirectory("test").toFile();
        File dir = new File(root, "dir");
        dir.mkdirs();
        File file = new File(dir, "item.txt");
        file.createNewFile();

        String relativePath = FileUtils.getRelativePath(dir, file);
        assertEquals("dir/item.txt", relativePath);
    }

    @Test
    public void testToFile() throws Exception {
        URI uri = new URI("file:///tmp?some=query");

        File file = FileUtils.toFile(uri);
        assertEquals(new File("/tmp"), file);
    }

    @Test
    public void testToFileUnmppableChars() throws Exception {
        File root = Files.createTempDirectory("test").toFile();
        String fileName = "item ção ü.txt";
        File file = new File(root, fileName);
        file.createNewFile();
        String uri = "file:" + root.getAbsolutePath() + "/item%20ção%20ü.txt";
        File recoveredFile = FileUtils.toFile(new URI(uri).normalize());
        assertTrue(recoveredFile.exists());
    }

}
