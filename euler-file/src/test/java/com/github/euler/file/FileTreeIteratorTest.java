package com.github.euler.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;

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
        assertTrue(iterator.hasNext());
        assertEquals(root, iterator.next());
        assertEquals(file1, iterator.next());
        assertEquals(file2, iterator.next());
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
        assertTrue(iterator.hasNext());
        assertEquals(root, iterator.next());
        assertEquals(file0, iterator.next());
        assertEquals(dir1, iterator.next());
        assertEquals(file1, iterator.next());
        assertEquals(file11, iterator.next());
        assertEquals(dir2, iterator.next());
        assertEquals(file2, iterator.next());
        assertEquals(dir3, iterator.next());
        assertEquals(file3, iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

}
