package com.github.euler.core;

public class FlushCommand implements TaskCommand, ProcessorCommand {

    public final boolean force;

    public FlushCommand(boolean force) {
        super();
        this.force = force;
    }

    public FlushCommand() {
        this(false);
    }

}
