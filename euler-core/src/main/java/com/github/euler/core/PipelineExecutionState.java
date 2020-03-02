package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.ActorRef;

public class PipelineExecutionState {

    private Map<URI, State> mapping = new HashMap<>();

    private class State {
        int position = 0;
        ActorRef<ProcessorCommand> replyTo;
        ProcessingContext ctx;
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
        return mapping.get(itemURI).ctx.merge(ctx);
    }

    public ProcessingContext getProcessingContext(URI itemURI) {
        return mapping.get(itemURI).ctx;
    }

    public void finish(URI itemURI) {
        mapping.remove(itemURI);
    }

}
