package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import com.github.euler.common.CommonMetadata;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Builder;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class BasicFilePropertiesExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create() {
        return Behaviors.setup((context) -> new BasicFilePropertiesExecution(context));
    }

    public BasicFilePropertiesExecution(ActorContext<TaskCommand> context) {
        super(context);
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {
        File file = getFile(msg);

        Builder builder = ProcessingContext.builder();
        builder.putAll(msg.ctx);

        builder.metadata(CommonMetadata.NAME, file.getName());
        builder.metadata(CommonMetadata.SIZE, file.length());
        builder.metadata(CommonMetadata.IS_DIRECTORY, file.isDirectory());
        builder.metadata(CommonMetadata.FULL_PATH, file.getAbsolutePath());

        String parentScheme = msg.uri.getScheme();
        if (parentScheme.equals("file")) {
            File parent = Paths.get(msg.uri).toFile();
            String relativePath = FileUtils.getRelativePath(parent, file);
            builder.metadata(CommonMetadata.PATH, relativePath);
            builder.metadata(CommonMetadata.RELATIVE_PATH, relativePath);
        } else {
            if (msg.ctx.context().containsKey(CommonMetadata.PATH)) {
                builder.metadata(CommonMetadata.PATH, msg.ctx.context(CommonMetadata.PATH));
            }
            if (msg.ctx.context().containsKey(CommonMetadata.RELATIVE_PATH)) {
                builder.metadata(CommonMetadata.RELATIVE_PATH, msg.ctx.context(CommonMetadata.RELATIVE_PATH));
            }
        }

        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        builder.metadata(CommonMetadata.CREATED_DATETIME, new Date(fileAttributes.creationTime().toMillis()));
        builder.metadata(CommonMetadata.LAST_MODIFIED_DATETIME, new Date(fileAttributes.lastModifiedTime().toMillis()));

        ProcessingContext ctx = builder.build();
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        return this;
    }

    private File getFile(JobTaskToProcess msg) {
        String scheme = msg.itemURI.getScheme();
        File file = null;
        if (scheme.equals("file")) {
            file = Paths.get(msg.itemURI).toFile();
        } else if (msg.ctx.metadata().containsKey(CommonMetadata.FULL_PATH)) {
            file = new File((String) msg.ctx.metadata(CommonMetadata.FULL_PATH));
        } else if (msg.ctx.context().containsKey(CommonMetadata.FULL_PATH)) {
            file = new File((String) msg.ctx.context(CommonMetadata.FULL_PATH));
        } else {
            throw new NullPointerException("itemURI must be a file scheme or " + CommonMetadata.FULL_PATH + " as context or metadata must be provided.");
        }

        return file;
    }

}
