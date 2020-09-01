package com.github.euler.tika;

import org.apache.tika.detect.Detector;

import com.github.euler.common.CommonMetadata;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.Task;
import com.github.euler.core.TaskCommand;

import akka.actor.typed.Behavior;

public class MimeTypeDetectTask implements Task {

    private String name;
    private StreamFactory sf;
    private Detector detector;

    public MimeTypeDetectTask(String name, StreamFactory sf, Detector detector) {
        this.name = name;
        this.sf = sf;
        this.detector = detector;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Behavior<TaskCommand> behavior() {
        return MimeTypeDetectExecution.create(this.sf, this.detector);
    }

    @Override
    public boolean accept(JobTaskToProcess msg) {
        return !msg.ctx.metadata().containsKey(CommonMetadata.MIME_TYPE);
    }

}
