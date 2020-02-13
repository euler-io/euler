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

}
