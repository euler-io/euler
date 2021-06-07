package com.github.euler.tika.embedded;

import java.net.URI;
import java.util.Map;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;
import com.github.euler.tika.EmbeddedNamingStrategy;

public class MimetypeEmbeddedNamingStrategy implements EmbeddedNamingStrategy {

    private final EmbeddedNamingStrategy defaultStrategy;
    private final Map<String, EmbeddedNamingStrategy> mapping;

    public MimetypeEmbeddedNamingStrategy(EmbeddedNamingStrategy defaultStrategy, Map<String, EmbeddedNamingStrategy> mapping) {
        super();
        this.defaultStrategy = defaultStrategy;
        this.mapping = mapping;
    }

    public MimetypeEmbeddedNamingStrategy(Map<String, EmbeddedNamingStrategy> mapping) {
        this(new DefaultEmbeddedNamingStrategy(), mapping);
    }

    @Override
    public String nameEmbedded(URI parentURI, ProcessingContext parentContext, Metadata embeddedMetadata) {
        String mimetype = embeddedMetadata.get(Metadata.CONTENT_TYPE);
        EmbeddedNamingStrategy strategy = mapping.getOrDefault(mimetype, defaultStrategy);
        return strategy.nameEmbedded(parentURI, parentContext, embeddedMetadata);
    }

}
