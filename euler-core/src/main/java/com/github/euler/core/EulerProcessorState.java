package com.github.euler.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.euler.command.EulerCommand;
import com.github.euler.command.JobItemToProcess;
import com.github.euler.command.JobTaskFinished;

import akka.actor.typed.ActorRef;

public class EulerProcessorState {

    private class State {

        public final ActorRef<EulerCommand> replyTo;
        public int tasks = 0;

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
        this.mapping.get(msg.itemURI).tasks--;
    }

    public ActorRef<EulerCommand> getReplyTo(JobTaskFinished msg) {
        return this.mapping.get(msg.itemURI).replyTo;
    }

    public boolean isProcessed(JobTaskFinished msg) {
        return this.mapping.get(msg.itemURI).tasks == 0;
    }

}
