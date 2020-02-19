package com.github.euler.core;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.github.euler.AkkaTest;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class SupervisedTaskTest extends AkkaTest {

    @Test
    @Ignore
    public void testWhenJobTaskToProcessTaskWillFowardToTask() throws Exception {
        TestProbe<TaskCommand> probe = testKit.createTestProbe();
        Task task = Tasks.foward("task", probe.ref());

        Task supervisedTask = new SupervisedTask("supervised-task", 2, task);

        TestProbe<ProcessorCommand> starterProbe = testKit.createTestProbe();
        ActorRef<TaskCommand> ref = testKit.spawn(supervisedTask.behavior());
        JobTaskToProcess msg = new JobTaskToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), ProcessingContext.EMPTY, starterProbe.ref());
        ref.tell(msg);

        probe.expectMessage(msg);
        probe.expectMessage(msg);
    }

}
