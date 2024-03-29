package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static Behavior<TaskCommand> create(Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy,
            MetadataParser metadataParser,
            ParseContextFactory parseContextFactory, EmbeddedNamingStrategy embeddedNamingStrategy, EmbeddedStrategyFactory embeddedStrategyFactory) {
        return Behaviors
                .setup((context) -> new ParseExecution(context, parser, sf, parsedContentStrategy, embeddedContentStrategy, metadataParser, parseContextFactory,
                        embeddedNamingStrategy, embeddedStrategyFactory));
    }

    private final Parser parser;
    private final StreamFactory sf;
    private final StorageStrategy parsedContentStrategy;
    private final StorageStrategy embeddedContentStrategy;
    private final MetadataParser metadataParser;
    private final ParseContextFactory parseContextFactory;
    private final EmbeddedNamingStrategy embeddedNamingStrategy;
    private final EmbeddedStrategyFactory embeddedStrategyFactory;

    private JobTaskToProcess currentMsg;

    protected ParseExecution(ActorContext<TaskCommand> context, Parser parser, StreamFactory sf, StorageStrategy parsedContentStrategy, StorageStrategy embeddedContentStrategy,
            MetadataParser metadataParser, ParseContextFactory parseContextFactory, EmbeddedNamingStrategy embeddedNamingStrategy,
            EmbeddedStrategyFactory embeddedStrategyFactory) {
        super(context);
        this.parser = parser;
        this.sf = sf;
        this.parsedContentStrategy = parsedContentStrategy;
        this.embeddedContentStrategy = embeddedContentStrategy;
        this.metadataParser = metadataParser;
        this.parseContextFactory = parseContextFactory;
        this.embeddedNamingStrategy = embeddedNamingStrategy;
        this.embeddedStrategyFactory = embeddedStrategyFactory;
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

        if (msg.ctx.context(CommonContext.ID) != null) {
            builder.metadata(CommonMetadata.PARENTS, msg.ctx.metadata(CommonMetadata.PARENTS, List.of()));
        }

        InputStream in = null;
        Writer out = null;
        try {
            in = sf.openInputStream(msg.itemURI, msg.ctx);
            out = new OutputStreamWriter(sf.openOutputStream(parsedContent, msg.ctx), "utf-8");
            ProcessingContext parsed = parse(msg.itemURI, in, out, msg.ctx);
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

    protected ProcessingContext parse(URI itemURI, InputStream in, Writer out, ProcessingContext ctx) throws IOException, SAXException, TikaException {
        try {
            Metadata metadata = new Metadata();
            EmbeddedStrategy embeddedStrategy = embeddedStrategyFactory.newEmbeddedStrategy(this);
            ParseContextFactoryWrapper wrapper = new ParseContextFactoryWrapper(parser, parseContextFactory, embeddedStrategy);
            ParseContext parseContext = wrapper.create(ctx);
            parser.parse(in, new BodyContentHandler(out), metadata, parseContext);
            return metadataParser.parse(metadata);
        } catch (EncryptedDocumentException e) {
            return ProcessingContext.builder().metadata(CommonMetadata.ENCRYPTED, true).build();
        } catch (Throwable e) {
            LOGGER.warn("An error ocurred while parsing {}: {}", itemURI, e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ProcessingContext.builder()
                    .context(CommonContext.PARSE_ERROR, true)
                    .metadata(CommonMetadata.PARSE_ERROR, true)
                    .metadata(CommonMetadata.PARSE_ERROR_STACK, sw.toString())
                    .build();
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

        String parentId = (String) currentMsg.ctx.context(CommonContext.ID);
        if (parentId != null) {
            List<String> parents = new ArrayList<>(currentMsg.ctx.metadata(CommonMetadata.PARENTS, List.of()));
            parents.add(parentId);
            builder.metadata(CommonMetadata.PARENTS, Collections.unmodifiableList(parents));
        }

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
