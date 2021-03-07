package com.github.euler.core;

import java.io.IOException;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class JobExecution extends AbstractBehavior<JobCommand> {

    public static Behavior<JobCommand> create(Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior) {
        return Behaviors.setup((context) -> new JobExecution(context, sourceBehavior, processorBehavior));
    }

    public static Behavior<JobCommand> create(Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior, EulerHooks hooks) {
        return Behaviors.setup((context) -> new JobExecution(context, sourceBehavior, processorBehavior, hooks));
    }

    private final Behavior<SourceCommand> sourceBehavior;
    private final Behavior<ProcessorCommand> processorBehavior;
    private final EulerHooks hooks;

    private ActorRef<EulerCommand> eulerRef;

    private JobExecution(ActorContext<JobCommand> context, Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior, EulerHooks hooks) {
        super(context);
        this.sourceBehavior = sourceBehavior;
        this.processorBehavior = processorBehavior;
        this.hooks = hooks;
    }

    private JobExecution(ActorContext<JobCommand> context, Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior) {
        this(context, sourceBehavior, processorBehavior, new EulerHooks());
    }

    @Override
    public Receive<JobCommand> createReceive() {
        ReceiveBuilder<JobCommand> builder = newReceiveBuilder();
        builder.onMessage(Job.class, this::onJob);
        builder.onMessage(JobProcessed.class, this::onJobProcessed);
        builder.onMessage(CancelJob.class, this::onCancelJob);
        return builder.build();
    }

    private Behavior<JobCommand> onJob(Job msg) throws IOException {
        this.hooks.initialize();
        this.eulerRef = getContext().spawn(EulerJobProcessor.create(sourceBehavior, processorBehavior), "euler");
        eulerRef.tell(new JobToProcess(msg.uri, msg.ctx, msg.replyTo));
        return Behaviors.same();
    }

    private Behavior<JobCommand> onJobProcessed(JobProcessed msg) throws IOException {
        return finishJob();
    }

    private Behavior<JobCommand> onCancelJob(CancelJob msg) throws IOException {
        return finishJob();
    }

    protected Behavior<JobCommand> finishJob() throws IOException {
        this.hooks.close();
        return Behaviors.stopped();
    }

}
