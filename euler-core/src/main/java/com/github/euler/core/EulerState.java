package com.github.euler.core;

import com.github.euler.command.DiscoveryFailed;
import com.github.euler.command.DiscoveryFinished;
import com.github.euler.command.JobCommand;
import com.github.euler.command.JobItemFound;
import com.github.euler.command.JobItemProcessed;
import com.github.euler.command.JobToProcess;

import akka.actor.typed.ActorRef;

public class EulerState {

    private ActorRef<JobCommand> replyTo;
    private boolean discoveryFinishedOrFailed = false;
    private int items = 0;

    public void onMessage(DiscoveryFinished msg) {
        this.discoveryFinishedOrFailed = true;
    }

    public void onMessage(JobItemFound msg) {
        this.items++;
    }

    public void onMessage(JobItemProcessed msg) {
        this.items--;
    }

    public void onMessage(JobToProcess msg) {
        this.replyTo = msg.replyTo;
    }

    public boolean isProcessed() {
        return this.items == 0 && this.discoveryFinishedOrFailed;
    }

    public ActorRef<JobCommand> getReplyTo() {
        return replyTo;
    }

    public void onMessage(DiscoveryFailed msg) {
        this.discoveryFinishedOrFailed = true;
    }

}
