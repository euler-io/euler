package com.github.euler.file;

import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class BasicFilePropertiesTask implements Task {

    public static final String NAME = "name";
    public static final String SIZE = "size";
    public static final String IS_DIRECTORY = "is-directory";
    public static final String PATH = "path";
    public static final String CREATED_DATETIME = "created-datetime";
    public static final String LAST_MODIFIED_DATETIME = "last-modified-datetime";

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
        return BasicFilePropertiesExecution.create();
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return "file".equals(msg.itemURI.getScheme());
    }

}
