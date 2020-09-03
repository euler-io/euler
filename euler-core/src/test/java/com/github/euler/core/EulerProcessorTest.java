package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class EulerProcessorTest extends AkkaTest {

    @Test
    public void testWhenJobItemToProcessItWillBeFowardedToTask() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();
        Task task = Tasks.foward("task", probe.ref());

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskToProcess.class);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenJobItemToProcessFowardToMultipleTasks() throws Exception {
        TestProbe<TaskCommand> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<TaskCommand> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.foward("task-2", probe2.ref());

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task1, task2));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessageClass(JobTaskToProcess.class);
        probe2.expectMessageClass(JobTaskToProcess.class);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenJobItemToProcessFowardOnlyToTasksThatAcceptIt() throws Exception {
        TestProbe<TaskCommand> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<TaskCommand> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.notAccept("task-2", () -> Tasks.fowardBehavior(probe2.ref()));

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task1, task2));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessageClass(JobTaskToProcess.class);
        probe2.expectNoMessage();
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenTaskSendJobTaskFinishedSendJobItemProcessed() throws Exception {
        Task task = Tasks.empty("task-1");

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task));

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobItemProcessed.class);
    }

    @Test
    public void testWhenAllTaskSendJobTaskFinishedSendJobItemProcessed() throws Exception {
        Task task1 = Tasks.empty("task-1");
        Task task2 = Tasks.empty("task-2");

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task1, task2));

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobItemProcessed.class);
        probe.expectNoMessage();
    }

    @Test
    public void testWhenTaskFailsReturnJobItemProcessed() throws Exception {
        Task task = Tasks.accept("will-fail", () -> WillFailBehavior.create());

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task));

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobItemProcessed.class);
    }

    @Test
    public void testWhenItemToProcessHasContextTaskWillReceiveIt() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();
        Task task = Tasks.foward("task", probe.ref());

        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(task));
        TestProbe<EulerCommand> startedProbe = testKit.createTestProbe();
        ProcessingContext ctx = ProcessingContext.builder().metadata("key", "value").build();
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ctx, startedProbe.ref());
        ref.tell(msg);

        JobTaskToProcess jttp = probe.expectMessageClass(JobTaskToProcess.class);
        assertEquals(ctx, jttp.ctx);
    }

    @Test
    public void testWhenReceiceFlushSendItToAllFlushableTasks() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();
        Task flushable = new Flushable(true, probe.ref());
        Task notFlushable = new Flushable(false, probe.ref());

        TestProbe<EulerCommand> startedProbe = testKit.createTestProbe();
        ActorRef<ProcessorCommand> ref = testKit.spawn(EulerProcessor.create(flushable, notFlushable));
        JobItemToProcess msg = new JobItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, startedProbe.ref());
        ref.tell(msg);
        ref.tell(new Flush());

        probe.expectMessageClass(JobTaskToProcess.class);
        probe.expectMessageClass(JobTaskToProcess.class);
        probe.expectMessageClass(Flush.class);
        probe.expectNoMessage();
    }

}
