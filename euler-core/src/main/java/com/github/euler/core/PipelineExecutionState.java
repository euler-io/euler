package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;

public class PipelineExecutionState implements TasksExecutionState {

    private Map<URI, State> mapping = new HashMap<>();

    private class State {
        int position = 0;
        ActorRef<ProcessorCommand> replyTo;
        ProcessingContext ctx;
        Cancellable timeoutCancellable;
    }

    public int getPosition(URI itemURI) {
        return mapping.get(itemURI).position;
    }

    public void setPosition(URI itemURI, int position) {
        mapping.get(itemURI).position++;
    }

    public void onMessage(JobTaskToProcess msg) {
        State state = new State();
        state.replyTo = msg.replyTo;
        state.ctx = msg.ctx;
        mapping.put(msg.itemURI, state);
    }

    public ActorRef<ProcessorCommand> getReplyTo(URI itemURI) {
        return mapping.get(itemURI).replyTo;
    }

    public ProcessingContext mergeContext(URI itemURI, ProcessingContext ctx) {
        State state = mapping.get(itemURI);
        Objects.requireNonNull(state, () -> "State for " + itemURI + " is invalid.");
        ProcessingContext merged = state.ctx.merge(ctx);
        state.ctx = merged;
        return merged;
    }

    public ProcessingContext getProcessingContext(URI itemURI) {
        return mapping.get(itemURI).ctx;
    }

    public void finish(URI itemURI) {
        State state = mapping.remove(itemURI);
        if (state.timeoutCancellable != null && !state.timeoutCancellable.isCancelled()) {
            state.timeoutCancellable.cancel();
        }
    }

    public boolean isActive(URI itemURI) {
        return mapping.containsKey(itemURI);
    }

    @Override
    public void processingStartedWithTimeout(JobTaskToProcess msg, Cancellable cancellable) {
        mapping.get(msg.itemURI).timeoutCancellable = cancellable;
    }

}
