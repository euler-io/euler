package com.github.euler.tika;

import java.net.URI;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;

public interface EmbeddedNamingStrategy {

    public String nameEmbedded(URI parentURI, ProcessingContext parentContext, Metadata embeddedMetadata);

}
