package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;

public class EulerProcessorState {

    private class State {

        public final ActorRef<EulerCommand> replyTo;
        public int tasks = 0;
        public Cancellable cancellable;

        public State(ActorRef<EulerCommand> replyTo, int tasks) {
            super();
            this.replyTo = replyTo;
            this.tasks = tasks;
        }

    }

    private Map<URI, State> mapping;

    public EulerProcessorState() {
        super();
        this.mapping = new HashMap<>();
    }

    public void onJobItemToProcess(JobItemToProcess msg) {
        this.mapping.put(msg.itemURI, new State(msg.replyTo, 1));
    }

    public void onJobTaskFinished(JobTaskFinished msg) {
        onJobTaskCommand(msg.itemURI);
    }

    public void onJobTaskFailed(JobTaskFailed msg) {
        onJobTaskCommand(msg.itemURI);
    }

    private void onJobTaskCommand(URI itemURI) {
        State state = this.mapping.get(itemURI);
        state.tasks--;
        if (state.cancellable != null && !state.cancellable.isCancelled()) {
            state.cancellable.cancel();
        }
    }

    public ActorRef<EulerCommand> getReplyTo(JobTaskFinished msg) {
        return getReplyTo(msg.itemURI);
    }

    public ActorRef<EulerCommand> getReplyTo(JobTaskFailed msg) {
        return getReplyTo(msg.itemURI);
    }

    private ActorRef<EulerCommand> getReplyTo(URI itemURI) {
        return this.mapping.get(itemURI).replyTo;
    }

    public ActorRef<EulerCommand> getReplyTo(EmbeddedItemFound msg) {
        return getReplyTo(msg.parentURI);
    }

    public boolean isProcessed(JobTaskFailed msg) {
        return isProcessed(msg.itemURI);
    }

    public boolean isProcessed(JobTaskFinished msg) {
        return isProcessed(msg.itemURI);
    }

    private boolean isProcessed(URI itemURI) {
        return this.mapping.get(itemURI).tasks == 0;
    }

    public void finish(JobTaskFailed msg) {
        mapping.remove(msg.itemURI);
    }

    public void finish(JobTaskFinished msg) {
        mapping.remove(msg.itemURI);
    }

    public void processingStartedWithTimeout(JobItemToProcess msg, Cancellable cancellable) {
        mapping.get(msg.itemURI).cancellable = cancellable;
    }

}
