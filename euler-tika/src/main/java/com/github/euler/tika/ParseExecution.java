package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class ParseExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(AutoDetectParser parser, StreamFactory sf) {
        return Behaviors.setup((context) -> new ParseExecution(context, parser, sf));
    }

    private final AutoDetectParser parser;
    private final StreamFactory sf;

    protected ParseExecution(ActorContext<TaskCommand> context, AutoDetectParser parser, StreamFactory sf) {
        super(context);
        this.parser = parser;
        this.sf = sf;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException, SAXException, TikaException {
        ProcessingContext.Builder builder = ProcessingContext.builder();
        URI parsedContent = createParsedContent(msg.itemURI);
        builder.context(ParseTask.PARSED_CONTENT_FILE, parsedContent);

        InputStream in = null;
        Writer out = null;
        try {
            in = sf.openInputStream(msg.itemURI);
            out = new OutputStreamWriter(sf.openOutputStream(parsedContent), "utf-8");

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
        Metadata metadata = new Metadata();
        parser.parse(in, new BodyContentHandler(out), metadata);
        return ProcessingContext.EMPTY;
    }

    private URI createParsedContent(URI itemURI) throws IOException {
        return Files.createTempFile("parsed", ".txt").toUri();
    }

}
