package com.github.euler.tika;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

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

    public static Behavior<TaskCommand> create(StreamFactory sf) {
        return Behaviors.setup((context) -> new ParseExecution(context, sf));
    }

    private final StreamFactory sf;

    protected ParseExecution(ActorContext<TaskCommand> context, StreamFactory sf) {
        super(context);
        this.sf = sf;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {

        ProcessingContext.Builder builder = ProcessingContext.builder();
        URI parsedContent = createParsedContent(msg.itemURI);
        builder.metadata(ParseTask.PARSED_CONTENT_FILE, parsedContent);

        ProcessingContext ctx = builder.build();
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));

        return this;
    }

    private URI createParsedContent(URI itemURI) throws IOException {
        return Files.createTempFile("parsed", ".txt").toUri();
    }

}
