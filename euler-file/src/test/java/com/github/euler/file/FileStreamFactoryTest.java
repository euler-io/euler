package com.github.euler.file;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.ProcessingContext;

public class FileStreamFactoryTest {

    @Test
    public void testFileStreamFactory() throws Exception {
        String content = "some content";
        URI uri = Files.createTempFile("filestreamfactory", ".tmp").toUri();

        StreamFactory sf = new FileStreamFactory();

        try (OutputStream out = sf.openOutputStream(uri, ProcessingContext.EMPTY)) {
            IOUtils.write(content, out, "utf-8");
        }

        String value = null;
        try (InputStream in = sf.openInputStream(uri, ProcessingContext.EMPTY)) {
            value = IOUtils.toString(in, "utf-8");
        }

        assertEquals(content, value);
    }

}
