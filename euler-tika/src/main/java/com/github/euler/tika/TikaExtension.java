package com.github.euler.tika;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;

public class TikaExtension implements EulerExtension {

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new TikaContextConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new MimeTypeDetectTaskConfigConverter(),
                new ParseTaskConfigConverter());
    }

}
