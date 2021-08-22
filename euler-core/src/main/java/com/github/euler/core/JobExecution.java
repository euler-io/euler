package com.github.euler.core;

import java.io.IOException;

import com.github.euler.core.source.DefaultSourceNotificationStrategy;
import com.github.euler.core.source.SourceNotificationStrategy;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class JobExecution extends AbstractBehavior<JobCommand> {

    public static Behavior<JobCommand> create(Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior, EulerHooks hooks,
            SourceNotificationStrategy sourceNotificationStrategy) {
        return Behaviors.setup((context) -> new JobExecution(context, sourceBehavior, processorBehavior, hooks, sourceNotificationStrategy));
    }

    public static Behavior<JobCommand> create(Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior) {
        return create(sourceBehavior, processorBehavior, new EulerHooks(), new DefaultSourceNotificationStrategy());
    }

    private final Behavior<SourceCommand> sourceBehavior;
    private final Behavior<ProcessorCommand> processorBehavior;
    private final EulerHooks hooks;
    private final SourceNotificationStrategy sourceNotificationStrategy;

    private ActorRef<EulerCommand> eulerRef;

    private JobExecution(ActorContext<JobCommand> context, Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior, EulerHooks hooks,
            SourceNotificationStrategy sourceNotificationStrategy) {
        super(context);
        this.sourceBehavior = sourceBehavior;
        this.processorBehavior = processorBehavior;
        this.hooks = hooks;
        this.sourceNotificationStrategy = sourceNotificationStrategy;
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
        this.eulerRef = getContext().spawn(EulerJobProcessor.create(sourceBehavior, processorBehavior, sourceNotificationStrategy), "euler");
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
