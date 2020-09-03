package com.github.euler.core;

public class Flush implements TaskCommand, ProcessorCommand {

    public final boolean force;

    public Flush(boolean force) {
        super();
        this.force = force;
    }

    public Flush() {
        this(false);
    }

}
