package com.github.euler.common;

import com.github.euler.core.Flush;
import com.github.euler.core.JobTaskToProcess;

public interface Batch {

    void process(JobTaskToProcess msg, BatchListener listener);

    void flush(Flush msg, BatchListener listener);

    void finish();

}
