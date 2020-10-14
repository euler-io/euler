package com.github.euler.common;

import java.net.URI;

import com.github.euler.core.Flush;
import com.github.euler.core.JobTaskFailed;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class BatchExecution extends AbstractBehavior<TaskCommand> implements BatchListener {

    public static Behavior<TaskCommand> create(Batch batch) {
        return Behaviors.setup((context) -> new BatchExecution(context, batch));
    }

    private final Batch batch;
    private final BatchState state;

    private BatchExecution(ActorContext<TaskCommand> context, Batch batch) {
        super(context);
        this.batch = batch;
        this.state = new BatchState();
    }

    @Override
    public Receive<TaskCommand> createReceive() {
        ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
        builder.onMessage(JobTaskToProcess.class, this::onJobTaskToProcess);
        builder.onMessage(Flush.class, this::onFlush);
        builder.onSignal(PostStop.class, this::onPostStop);
        return builder.build();
    }

    private Behavior<TaskCommand> onJobTaskToProcess(JobTaskToProcess msg) {
        state.onMessage(msg);
        batch.process(msg, this);
        return this;
    }

    private Behavior<TaskCommand> onFlush(Flush msg) {
        batch.flush(msg, this);
        return this;
    }

    private Behavior<TaskCommand> onPostStop(PostStop signal) {
        batch.finish();
        return this;
    }

    @Override
    public void finished(URI itemURI, ProcessingContext ctx) {
        JobTaskToProcess msg = state.finished(itemURI);
        msg.replyTo.tell(new JobTaskFinished(msg, ctx));
    }

    @Override
    public void failed(URI itemURI, ProcessingContext ctx) {
        JobTaskToProcess msg = state.finished(itemURI);
        msg.replyTo.tell(new JobTaskFailed(msg, ctx));
    }

}
