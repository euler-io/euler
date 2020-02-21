package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Duration;

import org.junit.Ignore;
import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.testing.DelayedExecution;
import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;

public class ConcurrentTaskTest extends AkkaTest {

    @Test
    public void testWhenJobTaskToProcessTaskWillFowardToTasks() throws Exception {
        TestProbe<TaskCommand> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<TaskCommand> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.foward("task-2", probe2.ref());

        Task concurrentTask = new ConcurrentTask("concurrent-task", task1, task2);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessageClass(JobTaskToProcess.class);
        probe2.expectMessageClass(JobTaskToProcess.class);
    }

    @Test
    public void testWhenJobTaskSendOnlyToTasksThatAcceptIt() throws Exception {
        TestProbe<TaskCommand> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<TaskCommand> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.notAccept("task-2", () -> FowardingBehavior.create(probe2.ref()));

        Task concurrentTask = new ConcurrentTask("concurrent-task", task1, task2);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessageClass(JobTaskToProcess.class);
        probe2.expectNoMessage();
    }

    @Test
    @Ignore
    public void testWhenTaskFailReplyToProcessorWithJobTaskFailed() throws Exception {
        Task task = Tasks.accept("task", () -> WillFailBehavior.create());
        Task concurrentTask = new ConcurrentTask("concurrent-task", task);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskFailed.class);
    }

    @Test
    public void testWhenJobTaskAndTaskFinishedReplyJobTaskFinished() throws Exception {
        Task task1 = Tasks.empty("task-1");
        Task task2 = Tasks.accept("task-2", () -> {
            return Behaviors.setup((ctx) -> DelayedExecution.create(Duration.ofMillis(500)));
        });
        Task concurrentTask = new ConcurrentTask("concurrent-task", task1, task2);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        probe.expectNoMessage(Duration.ofMillis(450));
        probe.expectMessageClass(JobTaskFinished.class);
    }

    @Test
    public void testWhenJobTaskMultipleTasksContextsAreMerged() throws Exception {
        Task task1 = Tasks.empty("task-1", ProcessingContext.builder().metadata("key1", "value1").build());
        Task task2 = Tasks.empty("task-2", ProcessingContext.builder().metadata("key2", "value2").build());

        Task concurrentTask = new ConcurrentTask("concurrent-task", task1, task2);
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(2, response.ctx.metadata().size());
        assertEquals("value1", response.ctx.metadata("key1"));
        assertEquals("value2", response.ctx.metadata("key2"));
    }
}
