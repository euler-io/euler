package com.github.euler.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Test;

import com.github.euler.exception.ProcessingAlreadyStarted;
import com.github.euler.message.EvidenceToProcess;

public class EulerStateTest {

    @Test
    public void testWhenCreatedNotProcessing() {
        EulerState state = new EulerState();
        assertFalse(state.isProcessing());
    }

    @Test
    public void testWhenEvidenceToProcessAsProcessing() throws Exception {
        EulerState state = new EulerState();

        URI uri = new URI("file:///some/path");
        EvidenceToProcess etp = new EvidenceToProcess(uri, null);
        state.onMessage(etp);
        assertTrue(state.isProcessing());
    }

    @Test(expected = ProcessingAlreadyStarted.class)
    public void testWhenEvidenceToProcessReceivedTwiceAnErrorIsRaised() throws Exception {
        EulerState state = new EulerState();

        URI uri = new URI("file:///some/path");
        EvidenceToProcess etp = new EvidenceToProcess(uri, null);
        state.onMessage(etp);
        state.onMessage(etp);
        assertTrue(state.isProcessing());
    }

}
