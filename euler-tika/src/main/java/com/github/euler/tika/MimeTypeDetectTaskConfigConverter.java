package com.github.euler.tika;

import java.net.URL;

import org.apache.tika.detect.Detector;

import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.AbstractTaskConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TasksConfigConverter;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class MimeTypeDetectTaskConfigConverter extends AbstractTaskConfigConverter {

    @Override
    public String type() {
        return "mime-type-detect";
    }

    @Override
    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String name = getName(config, tasksConfigConverter);
        String field = config.getString("field");
        Detector detector = ctx.getRequired(Detector.class);
        StreamFactory sf = ctx.getRequired(StreamFactory.class);
        return new MimeTypeDetectTask(name, field, sf, detector);
    }

    protected Config getDefaultConfig() {
        URL resource = MimeTypeDetectTaskConfigConverter.class.getClassLoader().getResource("mimetypetask.conf");
        return ConfigFactory.parseURL(resource);
    }

}
