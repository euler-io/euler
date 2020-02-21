package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;

class ConcurrentExecutionState {

    private class State {
        ActorRef<ProcessorCommand> replyTo;
        ProcessingContext ctx;
        int tasks = 0;
    }

    private Map<URI, State> mapping = new HashMap<>();

    public void taskStarted(URI itemURI, ActorRef<ProcessorCommand> replyTo, int tasksAccepted) {
        State state = new State();
        state.replyTo = replyTo;
        state.tasks = tasksAccepted;
        state.ctx = ProcessingContext.EMPTY;
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

    public void mergeContext(URI itemURI, ProcessingContext ctx) {
        State state = mapping.get(itemURI);
        state.ctx = state.ctx.merge(ctx);
    }

    public ProcessingContext getProcessingContext(URI itemURI) {
        return mapping.get(itemURI).ctx;
    }

}
