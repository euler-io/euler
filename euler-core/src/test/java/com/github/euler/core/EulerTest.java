package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.message.EvidenceDiscovery;
import com.github.euler.message.EvidenceItemFound;
import com.github.euler.message.EvidenceItemToProcess;
import com.github.euler.message.EvidenceMessage;
import com.github.euler.message.EvidenceToDiscover;
import com.github.euler.message.EvidenceToProcess;
import com.github.euler.testing.FowardingBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class EulerTest extends AkkaTest {

    @Test
    public void testWhenEvidenceToProcessFowardToDiscoverer() throws Exception {
        TestProbe<EvidenceDiscovery> probe = testKit.createTestProbe();
        Behavior<EvidenceDiscovery> discovererBehavior = FowardingBehavior.create(probe.ref());
        ActorRef<EvidenceMessage> ref = testKit.spawn(Euler.create(discovererBehavior, Behaviors.empty()));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceToProcess etp = new EvidenceToProcess(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(etp);
        probe.expectMessageClass(EvidenceToDiscover.class);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenEvidenceItemFoundFowardToProcessor() throws Exception {
        TestProbe<EvidenceItemToProcess> probe = testKit.createTestProbe();
        Behavior<EvidenceItemToProcess> processorBehavior = FowardingBehavior.create(probe.ref());

        ActorRef<EvidenceMessage> ref = testKit.spawn(Euler.create(Behaviors.empty(), processorBehavior));
        EvidenceItemFound eif = new EvidenceItemFound(new URI("file:///some/path"), new URI("file:///some/path/item"));
        ref.tell(eif);

        EvidenceItemToProcess eitp = probe.expectMessageClass(EvidenceItemToProcess.class);
        assertEquals(ref, eitp.sender);
    }

}
