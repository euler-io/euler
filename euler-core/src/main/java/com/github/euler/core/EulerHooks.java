package com.github.euler.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EulerHooks implements Initializable, Closeable {

    private final List<Initializable> initializables;
    private final List<Closeable> closeables;

    public EulerHooks() {
        super();
        this.initializables = new ArrayList<>();
        this.closeables = new ArrayList<>();
    }

    public List<Initializable> getInitializables() {
        return Collections.unmodifiableList(initializables);
    }

    public void registerInitializable(Initializable i) {
        this.initializables.add(i);
    }

    public List<Closeable> getCloseables() {
        return Collections.unmodifiableList(closeables);
    }

    public void registerCloseable(Closeable c) {
        this.closeables.add(c);
    }

    @Override
    public void initialize() throws IOException {
        for (Initializable i : initializables) {
            i.initialize();
        }
    }

    @Override
    public void close() throws IOException {
        for (Closeable c : closeables) {
            c.close();
        }
    }

}
