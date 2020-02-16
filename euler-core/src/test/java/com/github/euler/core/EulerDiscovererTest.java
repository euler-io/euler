package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.command.DiscovererCommand;
import com.github.euler.command.DiscoveryFailed;
import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobToDiscover;
import com.github.euler.command.NoSuitableDiscoverer;
import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailDiscovererBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class EulerDiscovererTest extends AkkaTest {

    @Test
    public void testWhenJobToDiscoverMessageArrivesItWillBeFowardedToTheDiscoverer() throws Exception {
        TestProbe<DiscovererCommand> probe = testKit.createTestProbe();
        Discoverer discoverer = Discoverers.foward(probe.ref());

        ActorRef<DiscovererCommand> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToDiscover msg = new JobToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe.expectMessage(msg);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenMultipleDiscovererAvailableChooseTheSuitableOne() throws Exception {
        TestProbe<DiscovererCommand> probe1 = testKit.createTestProbe();
        Discoverer suitableDiscoverer = Discoverers.foward(probe1.ref());

        TestProbe<DiscovererCommand> probe2 = testKit.createTestProbe();
        Discoverer notSuitableDiscoverer = Discoverers.acceptNone(() -> FowardingBehavior.create(probe2.ref()));

        ActorRef<DiscovererCommand> ref = testKit.spawn(EulerDiscoverer.create(suitableDiscoverer, notSuitableDiscoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToDiscover msg = new JobToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessage(msg);
        probe2.expectNoMessage();
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenNoSuitableDiscovererAvailableReturnNoSuitableDiscovererMessage() throws Exception {
        TestProbe<DiscovererCommand> probe = testKit.createTestProbe();
        Discoverer discoverer = Discoverers.acceptNone(() -> FowardingBehavior.create(probe.ref()));

        ActorRef<DiscovererCommand> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToDiscover msg = new JobToDiscover(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe.expectNoMessage();
        starterProbe.expectMessageClass(NoSuitableDiscoverer.class);
    }

    @Test
    public void testWhenDiscovererFailsReturnDiscoveryFailedMessage() throws Exception {
        Discoverer discoverer = Discoverers.acceptAll(() -> WillFailDiscovererBehavior.create());

        ActorRef<DiscovererCommand> ref = testKit.spawn(EulerDiscoverer.create(discoverer));

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        ref.tell(new JobToDiscover(new URI("file:///some/path"), probe.ref()));

        probe.expectMessageClass(DiscoveryFailed.class);
    }

}
