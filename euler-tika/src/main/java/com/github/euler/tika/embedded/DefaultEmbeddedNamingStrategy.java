package com.github.euler.tika.embedded;

import java.net.URI;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;

import com.github.euler.common.CommonContext;
import com.github.euler.core.ProcessingContext;
import com.github.euler.tika.EmbeddedNamingStrategy;
import com.github.euler.tika.RandomStringGenerator;

public class DefaultEmbeddedNamingStrategy implements EmbeddedNamingStrategy {

    private final RandomStringGenerator randomGenerator = new RandomStringGenerator();

    @Override
    public String nameEmbedded(URI parentURI, ProcessingContext parentContext, Metadata embeddedMetadata) {
        String resourceName = embeddedMetadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
        if (resourceName == null) {
            return "embedded_" + UUID.randomUUID().toString().toLowerCase();
        } else {
            String random = randomGenerator.generate(5);
            String parentId = getParentId(parentURI, parentContext);
            return random + "_" + parentId + "_" + FilenameUtils.getName(resourceName);
        }
    }

    private String getParentId(URI parentURI, ProcessingContext parentContext) {
        return parentContext.context(CommonContext.ID, () -> generateId(parentURI, parentContext));
    }

    private String generateId(URI uri, ProcessingContext ctx) {
        return DigestUtils.md5Hex(uri.normalize().toString()).toLowerCase();
    }

}
