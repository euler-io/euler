package com.github.euler.common;

import com.github.euler.core.FlushTask;
import com.github.euler.core.JobTaskToProcess;

public class FragmentBatch implements Batch {

    private final int fragmentSize;
    private final int fragmentOverlap;
    private final BatchSink sink;

    public FragmentBatch(int fragmentSize, int fragmentOverlap, BatchSink sink) {
        this.fragmentSize = fragmentSize;
        this.fragmentOverlap = fragmentOverlap;
        this.sink = sink;
    }

    @Override
    public void process(JobTaskToProcess msg, BatchListener listener) {

    }

    @Override
    public void flush(FlushTask msg, BatchListener listener) {

    }

    @Override
    public void finish() {
        sink.finish();
    }

}
