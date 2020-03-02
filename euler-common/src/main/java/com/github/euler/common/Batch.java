package com.github.euler.common;

import com.github.euler.core.FlushTask;
import com.github.euler.core.JobTaskToProcess;

public interface Batch {

    void process(JobTaskToProcess msg, BatchListener listener);

    void flush(FlushTask msg, BatchListener listener);

}
