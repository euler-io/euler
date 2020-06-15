package com.github.euler.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class FileTreeIteratorTest {

    @Test
    public void testIterateRootOnly() throws Exception {
        File root = Files.createTempDirectory("walker").toFile();

        Iterator<File> iterator = new FileTreeIterator(root);
        assertTrue(iterator.hasNext());
        assertEquals(root, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterateFiles() throws Exception {
        File root = Files.createTempDirectory("walker").toFile();

        File file1 = new File(root, "file1");
        file1.createNewFile();
        File file2 = new File(root, "file2");
        file2.createNewFile();

        Iterator<File> iterator = new FileTreeIterator(root);
        assertEquals(root, iterator.next());

        Set<File> files = new HashSet<>();
        while (iterator.hasNext()) {
            files.add(iterator.next());
        }
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterateMultiLevel() throws Exception {
        File root = Files.createTempDirectory("walker").toFile();
        File file0 = new File(root, "file0");
        file0.createNewFile();
        File dir1 = new File(root, "dir01");
        dir1.mkdir();
        File file1 = new File(dir1, "file1");
        file1.createNewFile();
        File file11 = new File(dir1, "file1-1");
        file11.createNewFile();

        File dir2 = new File(root, "dir02");
        dir2.mkdir();
        File file2 = new File(dir2, "file2");
        file2.createNewFile();

        File dir3 = new File(dir2, "dir03");
        dir3.mkdir();
        File file3 = new File(dir3, "file3");
        file3.createNewFile();

        Iterator<File> iterator = new FileTreeIterator(root);
        Set<File> files = new HashSet<>();
        File lastDir = root;
        assertEquals(root, iterator.next());
        while (iterator.hasNext()) {
            File file = iterator.next();
            files.add(file);
            if (file.isFile()) {
                // make sure that every dir cames before their files.
                assertEquals(lastDir, file.getParentFile());
            } else if (file.isDirectory()) {
                lastDir = file;
            }
        }
        assertFalse(iterator.hasNext());
        assertEquals(8, files.size());
        assertTrue(files.contains(file0));
        assertTrue(files.contains(dir1));
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file11));
        assertTrue(files.contains(dir2));
        assertTrue(files.contains(file2));
        assertTrue(files.contains(dir3));
        assertTrue(files.contains(file3));

    }

}
