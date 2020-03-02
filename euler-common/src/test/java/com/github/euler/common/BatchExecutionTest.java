package com.github.euler.common;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.euler.core.FlushTask;
import com.github.euler.core.JobTaskFinished;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.TaskCommand;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;

public class BatchExecutionTest extends AkkaTest {

    @Test
    public void testBatch() throws Exception {
        Batch batch = new Batch() {

            @Override
            public void process(JobTaskToProcess msg, BatchListener listener) {
                listener.finished(msg.itemURI, ProcessingContext.EMPTY);
            }

            @Override
            public void flush(FlushTask msg, BatchListener listener) {
                // Nothing to do.
            }

        };

        Behavior<TaskCommand> behavior = BatchExecution.create(batch);
        ActorRef<TaskCommand> ref = testKit.spawn(behavior);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        starterProbe.expectMessageClass(JobTaskFinished.class);
    }

    @Test
    public void testBatchPostPoned() throws Exception {
        Batch batch = new Batch() {

            List<URI> buffer = new ArrayList<>();

            @Override
            public void process(JobTaskToProcess msg, BatchListener listener) {
                buffer.add(msg.itemURI);
                if (buffer.size() > 1) {
                    buffer.forEach((itemURI) -> listener.finished(itemURI, ProcessingContext.EMPTY));
                }
            }

            @Override
            public void flush(FlushTask msg, BatchListener listener) {
                // Nothing to do.
            }

        };

        Behavior<TaskCommand> behavior = BatchExecution.create(batch);
        ActorRef<TaskCommand> ref = testKit.spawn(behavior);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        JobTaskToProcess msg1 = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item1"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg1);

        starterProbe.expectNoMessage();

        JobTaskToProcess msg2 = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item2"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg2);

        JobTaskFinished response1 = starterProbe.expectMessageClass(JobTaskFinished.class);
        assertEquals(msg1.itemURI, response1.itemURI);
        JobTaskFinished response2 = starterProbe.expectMessageClass(JobTaskFinished.class);
        assertEquals(msg2.itemURI, response2.itemURI);
    }

    @Test
    public void testBatchFlush() throws Exception {
        Batch batch = new Batch() {

            List<URI> buffer = new ArrayList<>();

            @Override
            public void process(JobTaskToProcess msg, BatchListener listener) {
                buffer.add(msg.itemURI);
            }

            @Override
            public void flush(FlushTask msg, BatchListener listener) {
                buffer.forEach((itemURI) -> listener.finished(itemURI, ProcessingContext.EMPTY));
            }

        };

        Behavior<TaskCommand> behavior = BatchExecution.create(batch);
        ActorRef<TaskCommand> ref = testKit.spawn(behavior);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        starterProbe.expectNoMessage();

        ref.tell(new FlushTask(true));

        starterProbe.expectMessageClass(JobTaskFinished.class);
    }

}
