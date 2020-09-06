package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Duration;

import org.junit.Test;

import com.github.euler.testing.WillFailBehavior;

import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class EulerJobProcessorTest extends AkkaTest {

    @Test
    public void testWhenJobToProcessFowardToSource() throws Exception {
        TestProbe<SourceCommand> probe = testKit.createTestProbe();
        Behavior<SourceCommand> discovererBehavior = FowardingBehavior.create(probe.ref());
        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(discovererBehavior, Behaviors.empty()));

        TestProbe<JobCommand> starterProbe = testKit.createTestProbe();
        JobToProcess msg = new JobToProcess(new URI("file:///some/path"), starterProbe.ref());
        ref.tell(msg);
        probe.expectMessageClass(JobToScan.class);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenJobItemFoundFowardToProcessor() throws Exception {
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();
        Behavior<ProcessorCommand> processorBehavior = FowardingBehavior.create(probe.ref());

        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Behaviors.empty(), processorBehavior));
        JobItemFound eif = new JobItemFound(new URI("file:///some/path"), new URI("file:///some/path/item"));
        ref.tell(eif);

        probe.expectMessageClass(JobItemToProcess.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedJobItemProcessedAndScanFinishedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);
        ScanFinished df = new ScanFinished(uri);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(jip);
        ref.tell(df);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedScanFinishedAndJobItemProcessedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        ScanFinished df = new ScanFinished(uri);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(df);
        ref.tell(jip);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedJobItemProcessedAndScanFailedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(itemURI, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);
        ScanFailed df = new ScanFailed(uri);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(jip);
        ref.tell(df);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobToProcessJobItemFinishedScanFaileddAndJobItemProcessedReturnJobProcessed() throws Exception {
        TestProbe<JobCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");

        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Behaviors.empty(), Behaviors.empty()));

        JobToProcess jtp = new JobToProcess(uri, probe.ref());
        JobItemFound jif = new JobItemFound(uri, itemURI);
        ScanFailed df = new ScanFailed(uri);
        JobItemProcessed jip = new JobItemProcessed(uri, itemURI);

        ref.tell(jtp);
        ref.tell(jif);
        ref.tell(df);
        ref.tell(jip);

        probe.expectMessageClass(JobProcessed.class);
    }

    @Test
    public void testWhenJobItemFoundHasContextProcessorWillReceiveIt() throws Exception {
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");
        ProcessingContext ctx = ProcessingContext.builder().metadata("key", "value").build();
        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Sources.fixedItemBehavior(itemURI, ctx), FowardingBehavior.create(probe.ref())));

        TestProbe<JobCommand> starterProbe = testKit.createTestProbe();
        ref.tell(new JobToProcess(uri, starterProbe.ref()));

        JobItemToProcess jitp = probe.expectMessageClass(JobItemToProcess.class);
        assertEquals(ctx.metadata(), jitp.ctx.metadata());
    }

    @Test
    public void testWhenJobToProcessHasContextProcessorWillReceiveIt() throws Exception {
        TestProbe<ProcessorCommand> probe = testKit.createTestProbe();

        URI uri = new URI("file:///some/path");
        URI itemURI = new URI("file:///some/path/item");
        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(Sources.fixedItemBehavior(itemURI), FowardingBehavior.create(probe.ref())));

        TestProbe<JobCommand> starterProbe = testKit.createTestProbe();
        ProcessingContext ctx = ProcessingContext.builder().metadata("key", "value").build();
        ref.tell(new JobToProcess(uri, ctx, starterProbe.ref()));

        JobItemToProcess jitp = probe.expectMessageClass(JobItemToProcess.class);
        assertEquals(ctx, jitp.ctx);
        starterProbe.expectNoMessage();
    }

    @Test
    public void testWhenSourceFailsReturnScanFailedMessage() throws Exception {
        Behavior<SourceCommand> sourceBehavior = WillFailBehavior.create();
        Behavior<ProcessorCommand> processorBehavior = Behaviors.empty();

        URI uri = new URI("file:///some/path");
        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(sourceBehavior, processorBehavior));
        TestProbe<JobCommand> probe = testKit.createTestProbe();
        ref.tell(new JobToProcess(uri, probe.ref()));

        JobProcessed response = probe.expectMessageClass(JobProcessed.class);
        assertEquals(uri, response.uri);
    }

    @Test
    public void testWhenScanFinishedStartFlushingPeriodically() throws Exception {
        TestProbe<ProcessorCommand> testProbe = testKit.createTestProbe();

        Behavior<SourceCommand> sourceBehavior = Sources.emptyBehavior();
        Behavior<ProcessorCommand> processorBehavior = Behaviors.receive(ProcessorCommand.class)
                .onAnyMessage((msg) -> {
                    testProbe.ref().tell(msg);
                    return Behaviors.same();
                })
                .build();

        URI uri = new URI("file:///some/path");
        ActorRef<EulerCommand> ref = testKit.spawn(EulerJobProcessor.create(sourceBehavior, processorBehavior));
        TestProbe<JobCommand> probe = testKit.createTestProbe();
        ref.tell(new JobToProcess(uri, probe.ref()));

        testProbe.expectMessageClass(Flush.class, Duration.ofSeconds(10));
        probe.expectMessageClass(JobProcessed.class);

    }

}
