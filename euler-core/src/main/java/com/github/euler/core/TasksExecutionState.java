package com.github.euler.core;

import akka.actor.Cancellable;

public interface TasksExecutionState {

    void processingStartedWithTimeout(JobTaskToProcess msg, Cancellable cancellable);

}
