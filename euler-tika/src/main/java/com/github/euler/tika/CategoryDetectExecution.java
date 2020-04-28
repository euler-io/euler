package com.github.euler.tika;

import java.io.IOException;

import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import com.github.euler.common.CommonMetadata;
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

public class CategoryDetectExecution extends AbstractBehavior<TaskCommand> {

    public static Behavior<TaskCommand> create(StreamFactory sf, Detector detector) {
        return Behaviors.setup((context) -> new CategoryDetectExecution(context, sf, detector));
    }

    private final StreamFactory sf;
    private final Detector detector;

    public CategoryDetectExecution(ActorContext<TaskCommand> context, StreamFactory sf, Detector detector) {
        super(context);
        this.sf = sf;
        this.detector = detector;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {
        String category = null;

        Boolean isDirectory = (Boolean) msg.ctx.metadata(CommonMetadata.IS_DIRECTORY);
        if (isDirectory != null && isDirectory) {
            category = "text/directory";
        } else {
            Metadata metadata = new Metadata();
            if (msg.ctx.metadata().containsKey(CommonMetadata.NAME)) {
                metadata.set(Metadata.RESOURCE_NAME_KEY, msg.ctx.metadata(CommonMetadata.NAME).toString());
            }
            try (TikaInputStream tikaInputStream = TikaInputStream.get(sf.openInputStream(msg.itemURI))) {
                MediaType type = detector.detect(tikaInputStream, metadata);
                type = type.getBaseType();
                category = type.getType() + "/" + type.getSubtype();
            }
        }
        ProcessingContext ctx = ProcessingContext.builder()
                .metadata(CommonMetadata.CATEGORY, category)
                .build();
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        return this;
    }

}
