package com.github.euler.tika;

import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class ParseTask implements Task {

    public static final String PARSED_CONTENT_FILE = ParseTask.class.getName() + ".PARSED_CONTENT_FILE";

    private final String name;

    public ParseTask(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return ParseExecution.create();
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return true;
    }

}
