package com.github.euler.core;

import java.time.Duration;

import akka.actor.Cancellable;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Scheduler;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import scala.concurrent.ExecutionContextExecutor;

public abstract class TasksExecution extends AbstractBehavior<TaskCommand> {

    public TasksExecution(ActorContext<TaskCommand> context) {
        super(context);
    }

    protected void sendToTask(Task task, JobTaskToProcess msg) {
        ActorRef<TaskCommand> taskRef = getOrSpawnTaskRef(task);
        taskRef.tell(msg);
        Duration taskTimeoutDuration = task.getTimeout();
        if (!taskTimeoutDuration.isNegative() && !taskTimeoutDuration.isZero()) {
            ActorRef<TaskCommand> self = getContext().getSelf();
            TaskTimeoutRunnable runnable = new TaskTimeoutRunnable(self, msg, task.name(), taskTimeoutDuration);
            Scheduler scheduler = getContext().getSystem().scheduler();
            ExecutionContextExecutor ece = getContext().getExecutionContext();
            Cancellable cancellable = scheduler.scheduleOnce(taskTimeoutDuration, runnable, ece);
            getState().processingStartedWithTimeout(msg, cancellable);
        }
    }

    protected abstract ActorRef<TaskCommand> getOrSpawnTaskRef(Task task);

    protected Behavior<TaskCommand> superviseTaskBehavior(Task t) {
        Behavior<TaskCommand> behavior = Behaviors.supervise(t.behavior()).onFailure(SupervisorStrategy.restart());
        return behavior;
    }

    protected abstract TasksExecutionState getState();

    private class TaskTimeoutRunnable implements Runnable {

        private final ActorRef<TaskCommand> parentTaskRef;
        private final JobTaskToProcess msg;
        private final String taskName;
        private final Duration timeout;

        private TaskTimeoutRunnable(ActorRef<TaskCommand> parentTaskRef, JobTaskToProcess msg, String taskName, Duration timeout) {
            super();
            this.parentTaskRef = parentTaskRef;
            this.msg = msg;
            this.taskName = taskName;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            getContext().getLog().warn("Task {} timed out after {} ms for item {}.", taskName, timeout.toMillis(), msg.itemURI);
            parentTaskRef.tell(new TaskTimeout(msg, taskName));
        }

    }

}
