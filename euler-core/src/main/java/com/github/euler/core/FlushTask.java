package com.github.euler.core;

public class FlushTask implements TaskCommand {

    public final boolean force;

    public FlushTask(boolean force) {
        super();
        this.force = force;
    }

    public FlushTask() {
        super();
        this.force = false;
    }

}
