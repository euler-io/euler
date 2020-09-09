package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

public class PooledTaskTest extends AkkaTest {

    @Test
    public void testWhenJobTaskToProcessTaskWillFowardToTask() throws Exception {

        Task task = Tasks.accept("task", () -> Behaviors.setup((ctx) -> new CountingTask(ctx)));

        Task pooledTask = new PooledTask("supervised-task", 2, task);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pooledTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        ref.tell(msg);

        assertEquals(0, probe.expectMessageClass(JobTaskFinished.class).ctx.metadata("count"));
        assertEquals(0, probe.expectMessageClass(JobTaskFinished.class).ctx.metadata("count"));
    }

    private class CountingTask extends AbstractBehavior<TaskCommand> {

        private int count = 0;

        public CountingTask(ActorContext<TaskCommand> context) {
            super(context);
        }

        @Override
        public Receive<TaskCommand> createReceive() {
            ReceiveBuilder<TaskCommand> builder = newReceiveBuilder();
            builder.onMessage(JobTaskToProcess.class, (msg) -> {
                msg.replyTo.tell(new JobTaskFinished(msg, ProcessingContext.builder().metadata("count", count++).build()));
                return Behaviors.same();
            });
            return builder.build();
        }

    }

    @Test
    public void testWhenTaskFailReplyToProcessorWithJobTaskFailed() throws Exception {
        Task task = Tasks.accept("task", () -> WillFailBehavior.create());

        Task pooledTask = new PooledTask("concurrent-task", 1, task);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pooledTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskFailed.class);
    }

    @Test
    @Ignore
    public void testWhenFlushAllTasksReceiveFlush() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();

        Task task = Tasks.foward("task", probe.ref());

        Task pooledTask = new PooledTask("pooled-task", 2, task);
        ActorRef<TaskCommand> ref = testKit.spawn(pooledTask.behavior());

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);
        ref.tell(msg);

        probe.expectMessageClass(JobTaskToProcess.class);
        probe.expectMessageClass(JobTaskToProcess.class);

        ref.tell(new Flush());
        probe.expectMessageClass(Flush.class);
        probe.expectMessageClass(Flush.class);
    }

}
