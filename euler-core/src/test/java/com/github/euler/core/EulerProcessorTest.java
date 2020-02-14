package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.message.EvidenceItemToProcess;
import com.github.euler.message.EvidenceMessage;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class EulerProcessorTest extends AkkaTest {

    @Test
    public void testWhenEvidenceItemToProcessItWillBeFowardedToTask() throws Exception {
        TestProbe<EvidenceItemToProcess> probe = testKit.createTestProbe();
        Task task = new FowardingTask(probe.ref());

        ActorRef<EvidenceItemToProcess> ref = testKit.spawn(EulerProcessor.create(task));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceItemToProcess eitf = new EvidenceItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(eitf);

        probe.expectMessage(eitf);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenEvidenceItemToProcessFowardToMultipleTasks() throws Exception {
        TestProbe<EvidenceItemToProcess> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<EvidenceItemToProcess> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.foward("task-2", probe2.ref());

        ActorRef<EvidenceItemToProcess> ref = testKit.spawn(EulerProcessor.create(task1, task2));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceItemToProcess eitf = new EvidenceItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(eitf);

        probe1.expectMessage(eitf);
        probe2.expectMessage(eitf);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenEvidenceItemToProcessFowardOnlyToTasksThatAcceptIt() throws Exception {
        TestProbe<EvidenceItemToProcess> probe1 = testKit.createTestProbe();
        Task task1 = Tasks.foward("task-1", probe1.ref());

        TestProbe<EvidenceItemToProcess> probe2 = testKit.createTestProbe();
        Task task2 = Tasks.notAccept("task-2", () -> Tasks.fowardBehavior(probe2.ref()));

        ActorRef<EvidenceItemToProcess> ref = testKit.spawn(EulerProcessor.create(task1, task2));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceItemToProcess eitf = new EvidenceItemToProcess(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(eitf);

        probe1.expectMessage(eitf);
        probe2.expectNoMessage();
        starterProbe.expectNoMessage();
    }

}
