package com.github.euler.core;

import java.net.URI;

import org.junit.Test;

import com.github.euler.AkkaTest;
import com.github.euler.command.DiscovererCommand;
import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobCommand;
import com.github.euler.command.JobItemFound;
import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.JobToDiscover;
import com.github.euler.command.JobToProcess;
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

        TestProbe<EulerCommand> starterProbe = testKit.createTestProbe();
        ActorRef<EulerCommand> ref = testKit.spawn(Euler.create(Behaviors.empty(), processorBehavior));
        JobItemFound eif = new JobItemFound(new URI("file:///some/path"), new URI("file:///some/path/item"), starterProbe.ref());
        ref.tell(eif);

        probe.expectMessageClass(JobItemToProcess.class);
    }

}
