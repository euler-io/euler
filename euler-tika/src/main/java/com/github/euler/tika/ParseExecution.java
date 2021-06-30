package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.EmbeddedItemFound;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;
import com.github.euler.core.ProcessingContext.Builder;
import com.github.euler.core.TaskCommand;
import com.github.euler.tika.metadata.MetadataParser;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class ParseExecution extends AbstractBehavior<TaskCommand> implements EmbeddedItemListener {

    public static Behavior<TaskCommand> create(Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy,
            MetadataParser metadataParser,
            ParseContextFactory parseContextFactory, EmbeddedNamingStrategy embeddedNamingStrategy, EmbeddedStrategy embeddedStrategy) {
        return Behaviors
                .setup((context) -> new ParseExecution(context, parser, sf, parsedContentStrategy, embeddedContentStrategy, metadataParser, parseContextFactory,
                        embeddedNamingStrategy, embeddedStrategy));
    }

    private final Parser parser;
    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;
    private final StorageStrategy embeddedContentStrategy;
    private final MetadataParser metadataParser;
    private final ParseContextFactory parseContextFactory;
    private final EmbeddedNamingStrategy embeddedNamingStrategy;
    private final EmbeddedStrategy embeddedStrategy;

    private JobTaskToProcess currentMsg;

    protected ParseExecution(ActorContext<TaskCommand> context, Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy,
            MetadataParser metadataParser, ParseContextFactory parseContextFactory, EmbeddedNamingStrategy embeddedNamingStrategy, EmbeddedStrategy embeddedStrategy) {
        super(context);
        this.parser = parser;
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
        this.embeddedContentStrategy = embeddedContentStrategy;
        this.metadataParser = metadataParser;
        this.parseContextFactory = new ParseContextFactoryWrapper(parser, parseContextFactory, embeddedStrategy);
        this.embeddedNamingStrategy = embeddedNamingStrategy;
        this.embeddedStrategy = embeddedStrategy;
        this.embeddedStrategy.setListener(this);
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException, SAXException, TikaException {
        this.currentMsg = msg;
        ProcessingContext.Builder builder = ProcessingContext.builder();
        URI parsedContent = createParsedContent(msg.itemURI);
        builder.context(CommonContext.PARSED_CONTENT_FILE, parsedContent);

        InputStream in = null;
        Writer out = null;
        try {
            in = sf.openInputStream(msg.itemURI, msg.ctx);
            out = new OutputStreamWriter(sf.openOutputStream(parsedContent, msg.ctx), "utf-8");

            ProcessingContext parsed = parse(in, out, msg.ctx);
            ProcessingContext ctx = msg.ctx.merge(builder.build()).merge(parsed);
            msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }

        return this;
    }

    protected ProcessingContext parse(InputStream in, Writer out, ProcessingContext ctx) throws IOException, SAXException, TikaException {
        try {
            Metadata metadata = new Metadata();
            ParseContext parseContext = parseContextFactory.create(ctx);
            parser.parse(in, new BodyContentHandler(out), metadata, parseContext);
            return metadataParser.parse(metadata);
        } catch (EncryptedDocumentException e) {
            return ProcessingContext.builder().metadata(CommonMetadata.ENCRYPTED, true).build();
        }
    }

    private URI createParsedContent(URI itemURI) throws IOException {
        return parsedContentStrategy.createFile(itemURI);
    }

    @Override
    public void newEmbedded(InputStream in, Metadata metadata) {
        String name = embeddedNamingStrategy.nameEmbedded(currentMsg.itemURI, currentMsg.ctx, metadata);

        URI embeddedFile = embeddedContentStrategy.createFileWithName(name);
        try (OutputStream out = sf.openOutputStream(embeddedFile, ProcessingContext.EMPTY)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Builder builder = ProcessingContext.builder()
                .metadata(CommonMetadata.CREATED_DATETIME, metadata.getDate(TikaCoreProperties.CREATED))
                .metadata(CommonMetadata.LAST_MODIFIED_DATETIME, metadata.getDate(TikaCoreProperties.MODIFIED))
                .metadata(CommonMetadata.NAME, name)
                .context(CommonContext.TEMPORARY_URI, embeddedFile)
                .setAction(Action.OVERWRITE);

        setPathAttribute(builder, name, CommonMetadata.RELATIVE_PATH, CommonMetadata.PATH);

        Integer depth = currentMsg.ctx.context(CommonContext.EXTRACTION_DEPTH, 0);
        builder.context(CommonContext.EXTRACTION_DEPTH, depth + 1);

        ProcessingContext ctx = builder.build();

        this.currentMsg.replyTo.tell(new EmbeddedItemFound(embeddedFile, this.currentMsg, ctx));
    }

    private void setPathAttribute(Builder builder, String name, String... attrNames) {
        Map<String, Object> currMetadata = this.currentMsg.ctx.metadata();

        for (String attrName : attrNames) {
            if (currMetadata.containsKey(attrName)) {
                String path = currMetadata.get(attrName) + "#" + name;
                builder.metadata(attrName, path);
            }
        }
    }

}
