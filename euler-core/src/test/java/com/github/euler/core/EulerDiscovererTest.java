package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.message.EvidenceDiscovery;
import com.github.euler.message.EvidenceDiscoveryFailed;
import com.github.euler.message.EvidenceMessage;
import com.github.euler.message.EvidenceToDiscover;
import com.github.euler.message.NoSuitableDiscoverer;
import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailDiscovererBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class EulerDiscovererTest extends AkkaTest {

    @Test
    public void testWhenEvidenceToDiscoverMessageArrivesItWillBeFowardedToTheDiscoverer() throws Exception {
        TestProbe<EvidenceToDiscover> probe = testKit.createTestProbe();
        Discoverer discoverer = Discoverers.foward(probe.ref());

        ActorRef<EvidenceDiscovery> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceToDiscover etd = new EvidenceToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(etd);

        probe.expectMessage(etd);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenMultipleDiscovererAvailableChooseTheSuitableOne() throws Exception {
        TestProbe<EvidenceToDiscover> probe1 = testKit.createTestProbe();
        Discoverer suitableDiscoverer = Discoverers.foward(probe1.ref());

        TestProbe<EvidenceToDiscover> probe2 = testKit.createTestProbe();
        Discoverer notSuitableDiscoverer = Discoverers.acceptNone(() -> FowardingBehavior.create(probe2.ref()));

        ActorRef<EvidenceDiscovery> ref = testKit.spawn(EulerDiscoverer.create(suitableDiscoverer, notSuitableDiscoverer));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceToDiscover etd = new EvidenceToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(etd);

        probe1.expectMessage(etd);
        probe2.expectNoMessage();
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenNoSuitableDiscovererAvailableReturnNoSuitableDiscovererMessage() throws Exception {
        TestProbe<EvidenceToDiscover> probe = testKit.createTestProbe();
        Discoverer discoverer = Discoverers.acceptNone(() -> FowardingBehavior.create(probe.ref()));

        ActorRef<EvidenceDiscovery> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        TestProbe<EvidenceMessage> starterProbe = testKit.createTestProbe();
        EvidenceToDiscover msg = new EvidenceToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe.expectNoMessage();
        starterProbe.expectMessageClass(NoSuitableDiscoverer.class);
    }

    @Test
    public void testWhenDiscovererFailsReturnEvidenceDiscoveryFailedMessage() throws Exception {
        Discoverer discoverer = Discoverers.acceptAll(() -> WillFailDiscovererBehavior.create());

        TestProbe<EvidenceMessage> probe = testKit.createTestProbe();
        ActorRef<EvidenceDiscovery> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        ref.tell(new EvidenceToDiscover(new URI("file:///some/path"), probe.ref()));

        probe.expectMessageClass(EvidenceDiscoveryFailed.class);
    }

}
