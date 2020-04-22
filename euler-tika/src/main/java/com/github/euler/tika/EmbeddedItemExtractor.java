package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class EmbeddedItemExtractor implements EmbeddedDocumentExtractor {

    private final EmbeddedItemListener listener;
    private final boolean parseEmbedded;

    public EmbeddedItemExtractor(EmbeddedItemListener listener, boolean parseEmbedded) {
        super();
        this.listener = listener;
        this.parseEmbedded = parseEmbedded;
    }

    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        return this.parseEmbedded;
    }

    @Override
    public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws SAXException, IOException {
        String resourceName = metadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
        if (resourceName != null) {
            handler.characters(resourceName.toCharArray(), 0, resourceName.length());
            if (outputHtml) {
                handler.startElement(XHTMLContentHandler.XHTML, "br", "br", new AttributesImpl());
                handler.endElement(XHTMLContentHandler.XHTML, "br", "br");
            }
        }

        if (this.listener != null) {
            this.listener.newEmbedded(stream, metadata);
        }
    }

}
