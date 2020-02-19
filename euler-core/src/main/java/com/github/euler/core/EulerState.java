package com.github.euler.core;

import akka.actor.typed.ActorRef;

public class EulerState {

    private ActorRef<JobCommand> replyTo;
    private boolean discoveryFinishedOrFailed = false;
    private int items = 0;
    private ProcessingContext ctx;

    public void onMessage(JobToProcess msg) {
        this.replyTo = msg.replyTo;
        this.ctx = msg.ctx;
    }

    public void onMessage(DiscoveryFinished msg) {
        this.discoveryFinishedOrFailed = true;
    }

    public void onMessage(JobItemFound msg) {
        this.items++;
    }

    public void onMessage(DiscoveryFailed msg) {
        this.discoveryFinishedOrFailed = true;
    }

    public void onMessage(JobItemProcessed msg) {
        this.items--;
    }

    public boolean isProcessed() {
        return this.items == 0 && this.discoveryFinishedOrFailed;
    }

    public ActorRef<JobCommand> getReplyTo() {
        return replyTo;
    }

    public ProcessingContext getCtx() {
        return ctx;
    }

}
