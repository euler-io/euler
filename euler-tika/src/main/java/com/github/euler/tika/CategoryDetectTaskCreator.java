package com.github.euler.tika;

import org.apache.tika.detect.Detector;

import com.github.euler.common.StreamFactory;
import com.github.euler.config.ConfigContext;
import com.github.euler.config.TaskCreator;
import com.github.euler.config.TaskFactory;
import com.github.euler.core.Task;
import com.typesafe.config.Config;

public class CategoryDetectTaskCreator implements TaskCreator {

    @Override
    public String type() {
        return "category-detect";
    }

    @Override
    public Task create(Config config, TaskFactory taskFactory, ConfigContext ctx) {
        Detector detector = ctx.getRequired(Detector.class);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        String name = null;
        return new CategoryDetectTask(name, sf, detector);
    }

}
