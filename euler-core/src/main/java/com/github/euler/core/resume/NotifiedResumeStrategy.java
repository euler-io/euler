package com.github.euler.core.resume;

import com.github.euler.core.ProcessingStatus;

public class NotifiedResumeStrategy implements ResumeStrategy {

    public NotifiedResumeStrategy() {
        super();
    }

    @Override
    public boolean shouldResumeScan() {
        return false;
    }

    @Override
    public boolean onProcessingStatus(ProcessingStatus msg) {
        return true;
    }

}
