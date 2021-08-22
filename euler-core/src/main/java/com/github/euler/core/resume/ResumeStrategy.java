package com.github.euler.core.resume;

import com.github.euler.core.ProcessingStatus;

public interface ResumeStrategy {

    boolean shouldResumeScan();

    boolean onProcessingStatus(ProcessingStatus msg);

}
