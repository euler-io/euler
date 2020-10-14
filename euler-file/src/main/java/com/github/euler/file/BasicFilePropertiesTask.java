package com.github.euler.file;

import com.github.euler.common.CommonMetadata;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class BasicFilePropertiesTask implements Task {

    private final String name;

    public BasicFilePropertiesTask(String name) {
        super();
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
        String itemScheme = msg.itemURI.getScheme();
        boolean isPathOnMetadata = msg.ctx.metadata().containsKey(CommonMetadata.FULL_PATH);
        boolean isPathOnContext = msg.ctx.context().containsKey(CommonMetadata.FULL_PATH);
        return "file".equals(itemScheme) || isPathOnMetadata || isPathOnContext;
    }

}
