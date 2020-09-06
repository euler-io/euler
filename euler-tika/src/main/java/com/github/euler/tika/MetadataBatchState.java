package com.github.euler.tika;

import java.util.HashMap;
import java.util.Map;

import com.github.euler.core.JobTaskToProcess;

public class MetadataBatchState {

    private class State {
        final JobTaskToProcess message;

        public State(JobTaskToProcess message) {
            super();
            this.message = message;
        }
    }

    private Map<String, State> control = new HashMap<>();

    public void itemProcessed(String id, JobTaskToProcess msg) {
        control.put(id, new State(msg));
    }

    public JobTaskToProcess getMessage(String id) {
        return control.get(id).message;
    }

    public void finish(String id) {
        control.remove(id);
    }

}
