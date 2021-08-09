package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnclosebleInputStream extends InputStream {

    private final InputStream wrapped;

    public UnclosebleInputStream(InputStream wrapped) {
        super();
        this.wrapped = wrapped;
    }

    @Override
    public int read() throws IOException {
        return this.wrapped.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.wrapped.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.wrapped.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return this.wrapped.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return this.wrapped.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return this.wrapped.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.wrapped.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.wrapped.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.wrapped.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.wrapped.reset();
    }

    @Override
    public boolean markSupported() {
        return this.wrapped.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return this.wrapped.transferTo(out);
    }

    @Override
    public void close() throws IOException {
        // No, cant do.
    }

}
