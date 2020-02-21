package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.testing.FowardingBehavior;
import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class EulerSourceTest extends AkkaTest {

    @Test
    public void testWhenJobToScanMessageArrivesItWillBeFowardedToTheSource() throws Exception {
        TestProbe<SourceCommand> probe = testKit.createTestProbe();
        Source discoverer = Sources.foward(probe.ref());

        ActorRef<SourceCommand> ref = testKit.spawn(EulerSource.create(discoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe.expectMessage(msg);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenMultipleSourcesAvailableChooseTheSuitableOne() throws Exception {
        TestProbe<SourceCommand> probe1 = testKit.createTestProbe();
        Source suitableDiscoverer = Sources.foward(probe1.ref());

        TestProbe<SourceCommand> probe2 = testKit.createTestProbe();
        Source notSuitableDiscoverer = Sources.acceptNone(() -> FowardingBehavior.create(probe2.ref()));

        ActorRef<SourceCommand> ref = testKit.spawn(EulerSource.create(suitableDiscoverer, notSuitableDiscoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe1.expectMessage(msg);
        probe2.expectNoMessage();
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenNoSuitableSourceAvailableReturnNoSuitableSourceMessage() throws Exception {
        TestProbe<SourceCommand> probe = testKit.createTestProbe();
        Source discoverer = Sources.acceptNone(() -> FowardingBehavior.create(probe.ref()));

        ActorRef<SourceCommand> ref = testKit.spawn(EulerSource.create(discoverer));

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        JobToScan msg = new JobToScan(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);

        probe.expectNoMessage();
        starterProbe.expectMessageClass(NoSuitableSource.class);
    }

    @Test
    public void testWhenSourceFailsReturnScanFailedMessage() throws Exception {
        Source discoverer = Sources.acceptAll(() -> WillFailBehavior.create());

        ActorRef<SourceCommand> ref = testKit.spawn(EulerSource.create(discoverer));

        TestProbe<EulerCommand> probe = testKit.createTestProbe();
        ref.tell(new JobToScan(new URI("file:///some/path"), probe.ref()));

        probe.expectMessageClass(ScanFailed.class);
    }

}
