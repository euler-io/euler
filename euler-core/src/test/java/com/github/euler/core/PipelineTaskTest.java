package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Duration;

import org.junit.Test;

import com.github.euler.testing.WillFailExecution;
import com.github.euler.testing.WillFailItemProcessor;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
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
        Task task1 = Tasks.fixed("task-1", ProcessingContext.builder().metadata("key", 10).build());
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
    public void testWhenTaskFailWithTimeoutReplyToProcessorWithJobTaskFinished() throws Exception {
        Task task1 = new TaskBuilder()
                .name("task-0")
                .processor(new WillFailItemProcessor())
                .timeout(Duration.ofSeconds(1))
                .build();
        Task task2 = Tasks.fixed("task-1", ProcessingContext.builder().metadata("key2", "value2").build());
        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(0, response.ctx.metadata().size());
    }

    @Test
    public void testWhenTaskFailWithTimeoutNextItemWillFinish() throws Exception {
        Task task1 = new TaskBuilder()
                .name("task-0")
                .processor(new WillFailItemProcessor(1))
                .timeout(Duration.ofSeconds(1))
                .build();
        Task task2 = Tasks.fixed("task-1", ProcessingContext.builder().metadata("key2", "value2").build());
        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);

        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item-1"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(0, response.ctx.metadata().size());

        msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item-2"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);

        response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(1, response.ctx.metadata().size());
        assertEquals("value2", response.ctx.metadata("key2"));
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
        Task task2 = Tasks.fixed("task-1", ProcessingContext.builder().metadata("key2", "value2").build());

        Task pipelineTask = new PipelineTask("pipeline-task", task1, task2);
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());

        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, probe.ref());
        ref.tell(msg);
        JobTaskFinished response = probe.expectMessageClass(JobTaskFinished.class);
        assertEquals(1, response.ctx.metadata().size());
        assertEquals("value1", response.ctx.metadata("key1"));
    }

    @Test
    public void testWhenReceiceFlushSendItToAllFlushableTasks() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();

        Task flushable = new Flushable(true, probe.ref()) {

            @Override
            public Behavior<TaskCommand> behavior() {
                return Behaviors.receive(TaskCommand.class)
                        .onMessage(JobTaskToProcess.class, (msg) -> {
                            msg.replyTo.tell(new JobTaskFinished(msg, ProcessingContext.EMPTY));
                            return Behaviors.same();
                        }).onMessage(Flush.class, (msg) -> {
                            ref.tell(msg);
                            return Behaviors.same();
                        })
                        .build();
            }
        };
        Task notFlushable = new Flushable(false, probe.ref()) {

            @Override
            public Behavior<TaskCommand> behavior() {
                return Behaviors.receive(TaskCommand.class)
                        .onMessage(JobTaskToProcess.class, (msg) -> {
                            msg.replyTo.tell(new JobTaskFinished(msg, ProcessingContext.EMPTY));
                            return Behaviors.same();
                        }).onMessage(Flush.class, (msg) -> {
                            ref.tell(msg);
                            return Behaviors.same();
                        })
                        .build();
            }

            @Override
            public String name() {
                return "not-flushable";
            }
        };

        Task pipelineTask = new PipelineTask("pipeline-task", flushable, notFlushable);
        ActorRef<TaskCommand> ref = testKit.spawn(pipelineTask.behavior());
        TestProbe<ProcessorCommand> startedProbe = testKit.createTestProbe();
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, startedProbe.ref());
        ref.tell(msg);

        startedProbe.expectMessageClass(JobTaskFinished.class);
        ref.tell(new Flush());
        probe.expectMessageClass(Flush.class);
        probe.expectNoMessage();
    }

}
