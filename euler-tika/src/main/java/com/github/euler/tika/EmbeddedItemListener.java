package com.github.euler.tika;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;

public interface EmbeddedItemListener {

    void newEmbedded(InputStream in, Metadata metadata);

}
