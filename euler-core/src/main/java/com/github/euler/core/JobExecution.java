package com.github.euler.core;

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

    private final Behavior<SourceCommand> sourceBehavior;
    private final Behavior<ProcessorCommand> processorBehavior;

    private JobExecution(ActorContext<JobCommand> context, Behavior<SourceCommand> sourceBehavior, Behavior<ProcessorCommand> processorBehavior) {
        super(context);
        this.sourceBehavior = sourceBehavior;
        this.processorBehavior = processorBehavior;
    }

    @Override
    public Receive<JobCommand> createReceive() {
        ReceiveBuilder<JobCommand> builder = newReceiveBuilder();
        builder.onMessage(Job.class, this::onJob);
        builder.onMessage(JobProcessed.class, this::onJobProcessed);
        return builder.build();
    }

    private Behavior<JobCommand> onJob(Job msg) {
        ActorRef<EulerCommand> eulerRef = getContext().spawn(EulerJobProcessor.create(sourceBehavior, processorBehavior), "euler");
        eulerRef.tell(new JobToProcess(msg.uri, msg.replyTo));
        return Behaviors.same();
    }

    private Behavior<JobCommand> onJobProcessed(JobProcessed msg) {
        return Behaviors.stopped();
    }

}
