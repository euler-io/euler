package com.github.euler.tika;

import java.util.HashMap;
import java.util.Map;

import com.github.euler.core.JobTaskToProcess;

public class FragmentBatchState {

    private class State {
        final JobTaskToProcess message;
        boolean parsed = false;
        boolean indexed = false;
        int fragments = 0;
        int fragmentsIndexed = 0;

        public State(JobTaskToProcess message) {
            super();
            this.message = message;
        }
    }

    private Map<String, State> control = new HashMap<>();
    private Map<String, String> mapping = new HashMap<>();

    public void itemStored(String id, JobTaskToProcess msg) {
        control.put(id, new State(msg));
    }

    public void itemParsed(String id) {
        control.get(id).parsed = true;
    }

    public void fragmentStored(String id, String fragId) {
        control.get(id).fragments++;
        mapping.put(fragId, id);
    }

    public boolean itemIndexed(String id) {
        String parentId = mapping.get(id);
        State state;
        if (parentId == null) {
            // is parent
            state = control.get(id);
            state.indexed = true;
        } else {
            // is fragment
            state = control.get(parentId);
            state.fragmentsIndexed++;
        }
        return state.parsed && state.indexed && state.fragments == state.fragmentsIndexed;
    }

    public JobTaskToProcess getMessage(String id) {
        return control.get(id).message;
    }

    public void finish(String id) {
        control.remove(id);
        mapping.values().removeIf(v -> v.equals(id));
    }

    public String getParent(String id) {
        if (control.containsKey(id)) {
            return id;
        } else if (mapping.containsKey(id)) {
            return mapping.get(id);
        } else {
            return null;
        }
    }

}
