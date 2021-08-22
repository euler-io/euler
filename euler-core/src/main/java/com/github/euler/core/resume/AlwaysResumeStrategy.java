package com.github.euler.core.resume;

import com.github.euler.core.ProcessingStatus;

public class AlwaysResumeStrategy implements ResumeStrategy {

    @Override
    public boolean shouldResumeScan() {
        return true;
    }

    @Override
    public boolean onProcessingStatus(ProcessingStatus msg) {
        return false;
    }

}
