package com.github.euler.common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.euler.core.JobTaskToProcess;

public class BatchState {

    private class State {
        final JobTaskToProcess original;

        public State(JobTaskToProcess original) {
            super();
            this.original = original;
        }
    }

    private Map<URI, State> mapping = new HashMap<>();

    public void onMessage(JobTaskToProcess msg) {
        mapping.put(msg.itemURI, new State(msg));
    }

    public JobTaskToProcess finished(URI itemURI) {
        return mapping.remove(itemURI).original;
    }

}
