package com.github.euler.file;

import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class BasicFilePropertiesTask implements Task {

    private String name;

    public BasicFilePropertiesTask(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return null;
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return msg.itemURI.getScheme().equals("file");
    }

}
