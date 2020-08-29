package com.github.euler.core;

import java.io.IOException;
import java.net.URI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

class ItemProcessorTaskCommand extends AbstractBehavior<TaskCommand> {

    private final ItemProcessor itemProcessor;

    public ItemProcessorTaskCommand(ActorContext<TaskCommand> context, ItemProcessor itemProcessor) {
        super(context);
        this.itemProcessor = itemProcessor;
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        return builder.build();
    }

    protected Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) throws IOException {
        ProcessingContext ctx = process(msg.uri, msg.itemURI, msg.ctx);
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));
        return Behaviors.same();
    }

    protected ProcessingContext process(URI parentURI, URI itemURI, ProcessingContext ctx) {
        return itemProcessor.process(new Item(parentURI, itemURI, ctx));
    }
}