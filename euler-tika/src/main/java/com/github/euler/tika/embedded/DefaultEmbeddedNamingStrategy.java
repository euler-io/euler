package com.github.euler.tika.embedded;

import java.net.URI;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;

import com.github.euler.core.ProcessingContext;
import com.github.euler.tika.EmbeddedNamingStrategy;

public class DefaultEmbeddedNamingStrategy implements EmbeddedNamingStrategy {

    @Override
    public String nameEmbedded(URI parentURI, ProcessingContext parentContext, Metadata embeddedMetadata) {
        String resourceName = embeddedMetadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
        if (resourceName == null) {
            resourceName = "embedded_" + UUID.randomUUID().toString().toLowerCase();
        }
        return FilenameUtils.getName(resourceName);
    }

}
