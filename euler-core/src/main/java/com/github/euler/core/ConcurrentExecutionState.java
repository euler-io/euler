package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;

class ConcurrentExecutionState {

    private class State {
        ActorRef<ProcessorCommand> replyTo;
        int tasks = 0;
    }

    private Map<URI, State> mapping = new HashMap<>();

    public void taskStarted(URI itemURI, ActorRef<ProcessorCommand> replyTo) {
        State state = new State();
        state.replyTo = replyTo;
        state.tasks++;
        mapping.put(itemURI, state);
    }

    public void taskFinished(URI itemURI) {
        mapping.get(itemURI).tasks--;
    }

    public boolean isTaskFinished(URI itemURI) {
        return mapping.get(itemURI).tasks == 0;
    }

    public ActorRef<ProcessorCommand> getReplyTo(URI itemURI) {
        return mapping.get(itemURI).replyTo;
    }

}
