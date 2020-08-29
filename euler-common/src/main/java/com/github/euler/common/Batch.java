package com.github.euler.common;

import com.github.euler.core.FlushCommand;
import com.github.euler.core.JobTaskToProcess;

public interface Batch {

    void process(JobTaskToProcess msg, BatchListener listener);

    void flush(FlushCommand msg, BatchListener listener);

    void finish();

}
