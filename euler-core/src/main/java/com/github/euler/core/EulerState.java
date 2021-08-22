package com.github.euler.core;

import akka.actor.typed.ActorRef;

public class EulerState {

    private ActorRef<JobCommand> replyTo;
    private boolean discoveryFinishedOrFailed = false;
    private int pendingItems = 0;
    private int totalProcessedItems = 0;
    private int totalItems = 0;
    private int totalEmbeddedItems = 0;
    private ProcessingContext ctx = ProcessingContext.EMPTY;

    public void onMessage(JobToProcess msg) {
        this.replyTo = msg.replyTo;
        this.ctx = msg.ctx;
    }

    public void onMessage(ScanFinished msg) {
        this.discoveryFinishedOrFailed = true;
    }

    public void onMessage(JobEmbeddedItemFound msg) {
        onMessage((JobItemFound) msg);
        this.totalEmbeddedItems++;
    }

    public void onMessage(JobItemFound msg) {
        this.totalItems++;
        this.pendingItems++;
    }

    public void onMessage(ScanFailed msg) {
        this.discoveryFinishedOrFailed = true;
    }

    public void onMessage(JobItemProcessed msg) {
        this.pendingItems--;
        this.totalProcessedItems++;
    }

    public boolean isProcessed() {
        return this.pendingItems == 0 && this.discoveryFinishedOrFailed;
    }

    public ActorRef<JobCommand> getReplyTo() {
        return replyTo;
    }

    public ProcessingContext getCtx() {
        return ctx;
    }

    public boolean isDiscoveryFinishedOrFailed() {
        return discoveryFinishedOrFailed;
    }

    public int getPendingItems() {
        return pendingItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalEmbeddedItems() {
        return totalEmbeddedItems;
    }

    public int getTotalProcessedItems() {
        return totalProcessedItems;
    }

}
