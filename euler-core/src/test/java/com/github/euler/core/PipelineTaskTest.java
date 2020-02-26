package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailBehavior;
import com.github.euler.testing.WillFailExecution;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;

public class PipelineTaskTest extends AkkaTest {

    @Test
    public void testWhenJobTaskToProcessTaskWillFowardToTask() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();
        Task task = Tasks.foward("task", probe.ref());

        Task pipelineTask = new PipelineTask("pipeline-task", task);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskToProcess.class);
    }

    @Test
    public void testWhenJobTaskSendOnlyToTasksThatAcceptIt() throws Exception {
        TestProbe<TaskCommand> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<TaskCommand> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.notAccept("task-2", () -> FowardingBehavior.create(probe2.ref()));

        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessageClass(JobTaskToProcess.class);
        probe2.expectNoMessage();
    }

    @Test
    public void testWhenJobTaskMultipleTasksContextIsSentToTheNextTask() throws Exception {
        Task task1 = Tasks.empty("task-1", ProcessingContext.builder().metadata("key", 10).build());
        Task task2 = Tasks.accept("task-2", () -> Behaviors.receive(TaskCommand.class)
                .onMessage(JobTaskToProcess.class, (msg) -> {
                    ProcessingContext ct = ProcessingContext.builder()
                            .metadata("key", ((Integer) msg.ctx.metadata("key")) + 10)
                            .build();
                    msg.replyTo.tell(new JobTaskFinished(msg, ct));
                    return Behaviors.same();
                })
                .build());

        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(1, response.ctx.metadata().size());
        assertEquals(20, response.ctx.metadata("key"));
    }

    @Test
    public void testWhenTaskFailReplyToProcessorWithJobTaskFinished() throws Exception {
        Task task1 = Tasks.accept("task", () -> WillFailBehavior.create());
        Task task2 = Tasks.empty("task-1", ProcessingContext.builder().metadata("key2", "value2").build());
        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(0, response.ctx.metadata().size());
    }

    @Test
    public void testWhenNoTaskAcceptSendTaskFinished() throws Exception {
        Task task = Tasks.notAccept("task");
        Task pipelineTask = new PipelineTask("pipeline-task", task);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        probe.expectMessageClass(JobTaskFinished.class);
    }

    @Test
    public void testWhenJobTaskTaskFailedPipelineInterrumpted() throws Exception {
        Task task1 = Tasks.accept("task-1", () -> WillFailExecution.create(ProcessingContext.builder().metadata("key1", "value1").build()));
        Task task2 = Tasks.empty("task-1", ProcessingContext.builder().metadata("key2", "value2").build());

        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(1, response.ctx.metadata().size());
        assertEquals("value1", response.ctx.metadata("key1"));
    }

}
