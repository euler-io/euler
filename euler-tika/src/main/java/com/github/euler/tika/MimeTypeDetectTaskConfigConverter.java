package com.github.euler.tika;

import org.apache.tika.detect.Detector;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class MimeTypeDetectTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "mime-type-detect";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        Detector detector = ctx.getRequired(Detector.class);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        String name = getName(config, tasksConfigConverter);
        return new MimeTypeDetectTask(name, sf, detector);
    }

}
