package com.github.euler.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.euler.core.EulerCommand;
import com.github.euler.core.JobItemFound;
import com.github.euler.core.JobToScan;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ScanFinished;
import com.github.euler.core.SourceCommand;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class FileSource extends AbstractBehavior<SourceCommand> {

    public static Behavior<SourceCommand> create() {
        return Behaviors.setup((context) -> new FileSource(context));
    }

    public FileSource(ActorContext<SourceCommand> context) {
        super(context);
    }

    @Override
    public Receive<SourceCommand> createReceive() {
        ReceiveBuilder<SourceCommand> builder = newReceiveBuilder();
        builder.onMessage(JobToScan.class, this::onJobToScan);
        return builder.build();
    }

    private Behavior<SourceCommand> onJobToScan(JobToScan msg) throws IOException {
        Path path = FileUtils.toPath(msg.uri);
        if (path.toFile().isDirectory()) {
            Files.walkFileTree(path, new FileSourceVisitor(msg.uri, new Listener(msg.replyTo)));
        } else {
            msg.replyTo.tell(new JobItemFound(msg.uri, msg.uri));
        }

        msg.replyTo.tell(new ScanFinished(msg));
        return this;
    }

    private static class Listener implements SourceListener {

        private final ActorRef<EulerCommand> replyTo;

        public Listener(ActorRef<EulerCommand> replyTo) {
            super();
            this.replyTo = replyTo;
        }

        @Override
        public void itemFound(URI uri, URI itemURI, ProcessingContext ctx) {
            replyTo.tell(new JobItemFound(uri, itemURI, ctx));
        }

    }

}
