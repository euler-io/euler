package com.github.euler.tika;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.tika.parser.AutoDetectParser;
import org.junit.Test;

import com.github.euler.file.FileStreamFactory;

public class FragmentBatchTest {

    @Test
    public void testFragmentBatch() throws Exception {
        AutoDetectParser parser = new AutoDetectParser();
        FileStreamFactory sf = new FileStreamFactory();
        int fragmentSize = 10;
        int fragmentOverlap = 2;
        BatchSink sink = new VoidSink();
        FragmentBatch batch = new FragmentBatch(parser, sf, fragmentSize, fragmentOverlap, sink);

        File parent = Files.createTempDirectory("frag").toFile();
        URI itemURI = createFile(parent, "0123456789");
    }

    private URI createFile(File parent, String content) throws IOException {
        File file = Files.createTempFile("frag-", ".txt").toFile();
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.write(content, out, "utf-8");
        }
        return file.toURI();
    }

}
