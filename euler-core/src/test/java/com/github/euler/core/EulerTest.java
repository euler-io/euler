package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.command.DiscovererCommand;
import com.github.euler.command.DiscoveryFailed;
import com.github.euler.command.DiscoveryFinished;
import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobCommand;
import com.github.euler.command.JobItemFound;
import com.github.euler.command.JobItemProcessed;
import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.JobProcessed;
import com.github.euler.command.JobToDiscover;
import com.github.euler.command.JobToProcess;
import com.github.euler.command.NoSuitableDiscoverer;
import com.github.euler.command.NoSuitableDiscovererForJob;
import com.github.euler.command.ProcessorCommand;
import com.github.euler.testing.FowardingBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class EulerTest extends AkkaTest {

    @Test
    public void testWhenJobToProcessFowardToDiscoverer() throws Exception {
        TestProbe<DiscovererCommand> probe = testKit.createTestProbe();
        Behavior<DiscovererCommand> discovererBehavior = FowardingBehavior.create(probe.ref());
        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(discovererBehavior, Behaviors.empty()));

        TestProbe<JobCommand> starterProbe = testKit.createTestProbe();
        JobToProcess msg = new JobToProcess(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);
        probe.expectMessageClass(JobToDiscover.class);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenJobItemFoundFowardToProcessor() throws Exception {
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        Behavior<ProcessorCommand> processorBehavior = FowardingBehavior.create(probe.ref());

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), processorBehavior));
        JobItemFound eif = new JobItemFound(new URI("file:///some/path"), new URI("file:///some/path/item"));
        ref.tell(eif);

        probe.expectMessageClass(JobItemToProcess.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedJobItemProcessedAndDiscoveryFinishedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);
        DiscoveryFinished df = new DiscoveryFinished(uri);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(jip);
        ref.tell(df);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedDiscoveryFinishedAndJobItemProcessedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        DiscoveryFinished df = new DiscoveryFinished(uri);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(df);
        ref.tell(jip);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedJobItemProcessedAndDiscoveryFailedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);
        DiscoveryFailed df = new DiscoveryFailed(uri);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(jip);
        ref.tell(df);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedDiscoveryFaileddAndJobItemProcessedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(uri, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        DiscoveryFailed df = new DiscoveryFailed(uri);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(df);
        ref.tell(jip);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenNoSuitableDiscovererReturnNoSuitableDiscovererForJob() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), Behaviors.empty()));

        URI uri = new URI("file:///some/path");
        ref.tell(new JobToProcess(uri, probe.ref()));
        ref.tell(new NoSuitableDiscoverer(uri));

        probe.expectMessageClass(NoSuitableDiscovererForJob.class);
    }

}
