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
        File parent = Paths.get(msg.uri).toFile();
        File file = Paths.get(msg.itemURI).toFile();

        Builder builder = ProcessingContext.builder();

        builder.metadata(CommonMetadata.NAME, file.getName());
        builder.metadata(CommonMetadata.SIZE, file.length());
        builder.metadata(CommonMetadata.IS_DIRECTORY, file.isDirectory());
        builder.metadata(CommonMetadata.PATH, FileUtils.getRelativePath(parent, file));

        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        builder.metadata(CommonMetadata.CREATED_DATETIME, new Date(fileAttributes.creationTime().toMillis()));
        builder.metadata(CommonMetadata.LAST_MODIFIED_DATETIME, new Date(fileAttributes.lastModifiedTime().toMillis()));

        ProcessingContext ctx = builder.build();
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        return this;
    }

}
