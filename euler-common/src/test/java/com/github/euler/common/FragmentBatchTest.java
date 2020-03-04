package com.github.euler.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class FragmentBatchTest {

    @Test
    public void testFragmentBatch() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        BatchSink sink = new VoidSink();
        FragmentBatch batch = new FragmentBatch(fragmentSize, fragmentOverlap, sink);

        URI itemURI = createFile("0123456789");
    }

    private URI createFile(String content) throws IOException {
        File file = Files.createTempFile("frag-", ".txt").toFile();
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.write(content, out, "utf-8");
        }
        return file.toURI();
    }

}
