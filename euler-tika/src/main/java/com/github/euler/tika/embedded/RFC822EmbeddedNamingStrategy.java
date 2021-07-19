package com.github.euler.tika.embedded;

import java.net.URI;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.metadata.Metadata;

import com.github.euler.common.CommonContext;
import com.github.euler.core.ProcessingContext;
import com.github.euler.tika.EmbeddedNamingStrategy;
import com.github.euler.tika.RandomStringGenerator;
import com.github.slugify.Slugify;

public class RFC822EmbeddedNamingStrategy implements EmbeddedNamingStrategy {

    private final Pattern identifierPattern;

    private final Slugify slg = new Slugify();
    private final RandomStringGenerator randomGenerator = new RandomStringGenerator();

    public RFC822EmbeddedNamingStrategy(String identifierRegex) {
        super();
        this.identifierPattern = Pattern.compile(identifierRegex);
    }

    @Override
    public String nameEmbedded(URI parentURI, ProcessingContext parentContext, Metadata embeddedMetadata) {
        String random = randomGenerator.generate(5);
        String parentId = getParentId(parentURI, parentContext);
        String subject = getSubject(embeddedMetadata);
        String identifier = getIdentifier(embeddedMetadata);
        String name = slg.slugify(random + "_" + parentId + "_" + identifier + "_" + subject);
        return name + ".eml";
    }

    private String getParentId(URI parentURI, ProcessingContext parentContext) {
        return parentContext.context(CommonContext.ID, () -> generateId(parentURI, parentContext));
    }

    private String generateId(URI uri, ProcessingContext ctx) {
        return DigestUtils.md5Hex(uri.normalize().toString()).toLowerCase();
    }

    @SuppressWarnings("deprecation")
    protected String getSubject(Metadata embeddedMetadata) {
        String subject = embeddedMetadata.get(Metadata.SUBJECT);
        if (subject.length() > 20) {
            subject = subject.substring(0, 20);
        }
        return subject;
    }

    @SuppressWarnings("deprecation")
    protected String getIdentifier(Metadata embeddedMetadata) {
        String identifier = embeddedMetadata.get(Metadata.IDENTIFIER);
        if (identifier != null) {
            Matcher matcher = identifierPattern.matcher(identifier.trim());
            if (matcher.find()) {
                identifier = matcher.group(1);
            } else {
                identifier = UUID.randomUUID().toString();
            }
        } else {
            identifier = UUID.randomUUID().toString();
        }
        return identifier;
    }

}
