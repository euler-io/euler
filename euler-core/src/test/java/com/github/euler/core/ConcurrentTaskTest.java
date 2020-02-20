package com.github.euler.core;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

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

        probe1.expectMessage(msg);
        probe2.expectMessage(msg);
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

        probe1.expectMessage(msg);
        probe2.expectNoMessage();
    }

    @Test
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
    @Ignore
    public void testWhenJobTaskAndTaskFinishedReplyJobTaskFinished() throws Exception {
        Task task1 = Tasks.empty("task-1");
        Task task2 = Tasks.empty("task-2");
        Task concurrentTask = new ConcurrentTask("concurrent-task", task1, task2);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(concurrentTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskFinished.class);
        probe.expectNoMessage();
    }
}
