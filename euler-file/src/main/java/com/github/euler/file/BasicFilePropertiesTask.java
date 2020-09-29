package com.github.euler.file;

import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class BasicFilePropertiesTask implements Task {

    private final String name;
    private final String[] schemes;

    public BasicFilePropertiesTask(String name, String[] schemes) {
        super();
        this.name = name;
        this.schemes = schemes;
    }

    public BasicFilePropertiesTask(String name) {
        this(name, new String[]{"file"});
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return BasicFilePropertiesExecution.create();
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        String itemScheme = msg.itemURI.getScheme();
        for (String scheme : schemes) {
            return scheme.equals(itemScheme);
        }
        return false;
    }

}
